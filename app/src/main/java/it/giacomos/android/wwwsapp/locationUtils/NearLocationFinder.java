package it.giacomos.android.wwwsapp.locationUtils;

import java.util.ArrayList;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class NearLocationFinder 
{
	private float mTolerance;
	
	public NearLocationFinder()
	{
		mTolerance = 5000f; /* 5 km */
	}
	
	public double getTolerance()
	{
		return mTolerance;
	}
	
	public void setTolerance(float tolerance)
	{
		mTolerance = tolerance;
	}
	
	/* returns the distance, in meters, between pt1 and pt2 */
	public float distanceBetween(LatLng pt1, LatLng pt2)
	{
		float dist = 0.0f;
		Location l1 = new Location("NearLocationFinder");
		l1.setLatitude(pt1.latitude);
		l1.setLongitude(pt1.longitude);
		
		Location l2 = new Location("NearLocationFinder");
		l2.setLatitude(pt2.latitude);
		l2.setLongitude(pt2.longitude);
		
		dist = l1.distanceTo(l2);
		
		return dist;
	}
	
	public LatLng nearestLocation(LatLng in, ArrayList<LatLng> points)
	{
		float minDist = 10000000f; /* 10.000 km should be enough :-) */
		float dist;
		LatLng ll = null;
		
		
		/* scan all points looking for the point with minimum distance 
		 * from the in input point.
		 */
		for(LatLng llng : points)
		{
			dist = distanceBetween(llng, in);
			if(dist < minDist)
			{
				Log.e("nearestLocation ", "dist " + dist + " minDist " + minDist);
				minDist = dist; /* save min dist */
				/* prepare return value in case this is the minimum distance */
				ll = new LatLng(llng.latitude, llng.longitude);
			}
		}
		return ll;
	}
	
}
