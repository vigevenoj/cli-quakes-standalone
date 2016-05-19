package com.sharkbaitextraordinaire.earthquakes;

import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import org.geojson.*;

public class EarthquakeMqttClient implements MqttCallback, Runnable {
	
	private Properties properties;
	MqttClient client;
	MqttConnectOptions connectionOptions;
	
	public EarthquakeMqttClient(Properties properties) {
		this.properties = properties;
	}

	public void connectionLost(Throwable arg0) {
		System.out.println("Connection to mqtt broker was lost");
		
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// No-op, we don't publish
		
	}

	public void messageArrived(String topicString, MqttMessage message) throws Exception {
    try {
      String payload = new  String(message.getPayload());
      System.out.println(payload);
      EarthquakeFeedFetcher.setLocation(new Point(-122.582999, 45.482845));
    } catch(Exception e) {
      // TODO log this?
    }
		
	}

	public void run() {
		String clientID = properties.getProperty("app.client.client_id");
		String brokerUrl = properties.getProperty("app.broker.url");
		
		connectionOptions = new MqttConnectOptions();
		connectionOptions.setKeepAliveInterval(30);
		connectionOptions.setUserName(properties.getProperty("app.client.username"));
    connectionOptions.setPassword(properties.getProperty("app.client.password").toCharArray());
		
		Properties sslProperties = new Properties();
		sslProperties.setProperty("com.ibm.ssl.protocol", properties.getProperty("com.ibm.ssl.protocol"));
		sslProperties.setProperty("com.ibm.ssl.trustStore", properties.getProperty("com.ibm.ssl.trustStore"));
		sslProperties.setProperty("com.ibm.ssl.trustStorePassword", properties.getProperty("com.ibm.ssl.trustStorePassword"));
		
		connectionOptions.setSSLProperties(sslProperties);
		
		try {
			client = new MqttClient(brokerUrl, clientID);
			client.setCallback(this);
			client.connect(connectionOptions);
		} catch (MqttException e) {
      System.out.println("NOT CONNECTED");
			e.printStackTrace();
		}
		
    if (client.isConnected()) {
      System.out.println("Connected to " + brokerUrl);

      String myTopic = properties.getProperty("app.broker.topic");
      MqttTopic topic = client.getTopic(myTopic);

      if (true) {
        try {
          int subQoS = 0;
          client.subscribe(myTopic, subQoS);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
	}

}
