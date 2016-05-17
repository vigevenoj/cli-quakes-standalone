package com.sharkbaitextraordinaire.earthquakes;

import org.geojson.Point;

public class Haversine {

	private static final int EARTH_RADIUS = 6371; //radius in km
	
	public double distance(Point p1, Point p2) {
		
		double deltaLatitude = Math.toRadians(p1.getCoordinates().getLatitude() - p2.getCoordinates().getLatitude());
		double deltaLongitude = Math.toRadians(p1.getCoordinates().getLongitude() - p2.getCoordinates().getLongitude());
		
		double latitude1 = Math.toRadians(p1.getCoordinates().getLatitude());
		double latitude2 = Math.toRadians(p2.getCoordinates().getLatitude());
		
		double a = haversine(deltaLatitude) + Math.cos(latitude1) * Math.cos(latitude2) * haversine(deltaLongitude);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return EARTH_RADIUS * c; 		
	}
	
	public static double haversine(double val) {
		return Math.pow(Math.sin(val / 2), 2);
	}
}
