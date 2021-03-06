package it.giacomos.android.wwwsapp.report;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public abstract class DataInterface 
{
	public static int TYPE_REPORT = 0;
	public static int TYPE_REQUEST = 1;
	public static int TYPE_ACTIVE_USER = 2;
	
	private final int closeDistance = 500;
	private double latitude, longitude;
	private String datetime, userDisplayName, layerName;
	private HashMap<String, String> data;
	private boolean mAlreadyPresent;


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

	public String getUserDisplayName()
	{
		return userDisplayName;
	}

	public String getLayerName()
	{
		return layerName;
	}
	
	public abstract int getType();

	public abstract boolean isWritable();
	
	public abstract MarkerOptions buildMarkerOptions(Context ctx, XmlUIDocumentRepr dataRepr);
	
	public abstract MarkerOptions getMarkerOptions();
	
	public abstract void  setMarker(Marker m);
	
	public abstract Marker getMarker();

	public abstract String getId();

	public void add(String key, String value)
	{
		data.put(key, value);
	}
	
	public DataInterface(String layNam, double lat, double lon, String datet, String userDisplayNam)
	{
		latitude = lat;
		longitude = lon;
		datetime = datet;
		userDisplayName = userDisplayNam;
		layerName = layNam;
		data = new HashMap<String, String>();
		mAlreadyPresent = false;
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

	public String getLocality()
	{
		if(data.containsKey("locality"))
			return data.get("locality");
		return "";
	}

	public HashMap<String, String> getData()
	{
		return data;
	}

	public String getDataRepr()
	{
		String s = "";
		String value = "";
		for(String k : data.keySet())
		{
			value = data.get(k);
			if(!value.isEmpty())
				s += k + ": " + data.get(k) + "\n";
		}
		return s;
	}

	public boolean sameAs(DataInterface other)
	{
		return this.getId().compareTo(other.getId()) == 0;
	}

	public boolean isMarkedAlreadyPresent()
	{
		return mAlreadyPresent;
	}

	public void markAlreadyPresent()
	{
		mAlreadyPresent = true;
	}
}
