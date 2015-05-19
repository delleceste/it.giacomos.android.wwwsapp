package it.giacomos.android.wwwsapp.locationUtils;

import android.location.Location;

public class LocationUtils {
	
	public boolean locationInsideRegion(Location location)
	{
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		return locationInsideRegion(latitude, longitude);
	}
	
	public boolean locationInsideRegion(double latitude, double longitude)
	{
		double lat = latitude;
		double lon = longitude;
	
		if(lat > GeoCoordinates.bottomRight.latitude && 
				lat < GeoCoordinates.topLeft.latitude && 
				lon > GeoCoordinates.topLeft.longitude && 
				lon < GeoCoordinates.bottomRight.longitude)
		{
			return true;
		}
		return false;
	}
}
