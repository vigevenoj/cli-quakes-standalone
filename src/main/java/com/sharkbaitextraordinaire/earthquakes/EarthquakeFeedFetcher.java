package com.sharkbaitextraordinaire.earthquakes;

import java.io.IOException;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.*;
import org.geojson.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sample application to fetch the USGS hourly earthquake geojson feed
 * and print the title, latitude and longitude of each earthquake 
 * present in the feed.
 */
public class EarthquakeFeedFetcher 
{
  public static void main( String[] args )
  {
    final String EARTHQUAKE_FEED_URL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    ClientConfig configuration = new ClientConfig();
    JerseyClient client =  JerseyClientBuilder.createClient(configuration);
    JerseyWebTarget target = client.target(EARTHQUAKE_FEED_URL);

    Invocation.Builder invocationBuilder = target.request();
    Response response = invocationBuilder.get();

    int status = response.getStatus();
    if (status == 200) {

      String feedString = response.readEntity(String.class); 

      try {
        FeatureCollection fc = mapper.readValue(feedString, FeatureCollection.class);
        for (Feature feature : fc.getFeatures()) {
          System.out.println(feature.getProperty("title"));
          GeoJsonObject g = feature.getGeometry();
          if (g instanceof Point) {
            Point p = (Point)g;
            System.out.println("At " + p.getCoordinates().getLatitude() + ", " + p.getCoordinates().getLongitude());
          }
          /* Properties: 
           * feature.getProperty("place") is a string description of where the earthquake happened
           * feature.getProperty("time") is the time of the earthquake
           * feature.getProperty("url") is an html page about the earthquake
           * feature.getProperty("detail") returns geojson about the earthquake
           * feature.getProperty("mag") is the magnitude of the earthquake
           * feature.getProperty("title") is the title of the earthquake event
           * feature.getGeometry() returns the geometry of the earthquake, latitude, longitude, and depth
           */
        }

      } catch (JsonParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (JsonMappingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      System.out.println("Got " + status + " response instead of 200");
    }
  }
}
