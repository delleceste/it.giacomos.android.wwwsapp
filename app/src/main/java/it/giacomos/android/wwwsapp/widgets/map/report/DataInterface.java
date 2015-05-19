package it.giacomos.android.wwwsapp.widgets.map.report;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public abstract class DataInterface 
{
	public static int TYPE_REPORT = 0;
	public static int TYPE_REQUEST = 1;
	public static int TYPE_ACTIVE_USER = 2;
	
	private final int closeDistance = 500;
	private double latitude, longitude;
	private String datetime;
	
	
	public String getDateTime()
	{
		return datetime;
	}
	
	public void setLatitude(double l)
	{
		latitude = l;
	}
	
	public void setLongitude(double l)
	{
		longitude = l;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public abstract int getType();
	public abstract String getLocality();
	
	public abstract boolean isWritable();
	
	public abstract MarkerOptions buildMarkerOptions(Context ctx);
	
	public abstract MarkerOptions getMarkerOptions();
	
	public abstract void  setMarker(Marker m);
	
	public abstract Marker getMarker();	
	
	public DataInterface(double lat, double lon, String datet)
	{
		latitude = lat;
		longitude = lon;
		datetime = datet;
	}
	
	public abstract boolean isPublished();
	
	/** this method evaluates whether two DataInterfaces can be considered 
	 * very close to each other. In fact, two markers too close to each other on the map
	 * are not useful..
	 * @param other
	 * @return
	 */
	public boolean isVeryCloseTo(DataInterface other)
	{
		Location l1 = new Location("");
		l1.setLatitude(getLatitude());
		l1.setLongitude(getLongitude());
		Location l2 = new Location("");
		l2.setLatitude(other.getLatitude());
		l2.setLongitude(other.getLongitude());
		
		return l1.distanceTo(l2) < closeDistance;
	}
	
	public boolean isVeryCloseTo(double lat, double lon)
	{
		Location l1 = new Location("");
		l1.setLatitude(getLatitude());
		l1.setLongitude(getLongitude());
		Location l2 = new Location("");
		l2.setLatitude(lat);
		l2.setLongitude(lon);
		
		return l1.distanceTo(l2) < closeDistance;
	}
	
	
}
