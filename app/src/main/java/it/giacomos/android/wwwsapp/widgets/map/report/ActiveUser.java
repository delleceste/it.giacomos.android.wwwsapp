package it.giacomos.android.wwwsapp.widgets.map.report;

import it.giacomos.android.wwwsapp.R;
import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActiveUser extends DataInterface {

	public boolean isRecent, isQuiteRecent;
	public String datetime;
	private MarkerOptions mMarkerOptions;
	private Marker mMarker;
	public int otherUsersInAreaCnt;
	
	public ActiveUser(String datet, double lat, double lon, 
			boolean recent, boolean quite_recent)
	{
		super(lat, lon, datet);
		datetime = datet;
		isRecent = recent;
		isQuiteRecent = quite_recent;
		otherUsersInAreaCnt = 0; /* only this */
	}
	
	public ActiveUser(String datet, double lat, double lon, 
			boolean recent, boolean quite_recent, 
			int otherUsersNearbyCnt)
	{
		super(lat, lon, datet);
		datetime = datet;
		isRecent = recent;
		isQuiteRecent = quite_recent;
		/* _other_ users. Total users in area is  otherUsersInAreaCnt + 1 (this) */
		otherUsersInAreaCnt = otherUsersNearbyCnt; 
	}
	
	@Override
	public int getType() {
		return TYPE_ACTIVE_USER;
	}

	@Override
	public String getLocality() {
		return "";
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	@Override
	public MarkerOptions buildMarkerOptions(Context ctx)
	{
		String  title, snippet;
		Resources res = ctx.getResources();
		int totalUsersInAreaCnt = otherUsersInAreaCnt + 1;
		mMarkerOptions = new MarkerOptions();
		mMarkerOptions.position(new LatLng(getLatitude(), getLongitude()));
		if(totalUsersInAreaCnt == 1)
			title = res.getString(R.string.activeUser);
		else
			title = totalUsersInAreaCnt + " " + res.getString(R.string.activeUsers);
		if(isRecent)
		{
			title += " " + res.getString(R.string.inTheLast10Min);
			if(totalUsersInAreaCnt == 1)
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_medium));
			else if(totalUsersInAreaCnt < 26)
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_big_green));
			else
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_bigger_green));
		}
		else if(isQuiteRecent)
		{
			title += " " + res.getString(R.string.inTheLast20Min);
			if(totalUsersInAreaCnt == 1)
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_small));
			else if(totalUsersInAreaCnt < 26)
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_big_violet));
			else
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_bigger_violet));
		}
		else
		{
			title += " " + res.getString(R.string.inTheLastHour);
			if(totalUsersInAreaCnt == 1)
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_micro));
			else if(totalUsersInAreaCnt < 26)
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_big_red));
			else
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_circle_bigger_red));
		}
		if(totalUsersInAreaCnt == 1)
			snippet = datetime + ": " + res.getString(R.string.activeUserSeemdAvailableInThisZone);
		else
			snippet = datetime + ": " + totalUsersInAreaCnt + " " + res.getString(R.string.activeUsersSeemdAvailableInThisZone);
		/* add * Touch the baloon to publish a request in this area hint */
		snippet += "\n*" + res.getString(R.string.touchBaloonToMakeRequestInThisArea);

		mMarkerOptions.title(title);
		mMarkerOptions.snippet(snippet);

		return mMarkerOptions;
	}

	@Override
	public MarkerOptions getMarkerOptions() 
	{
		return mMarkerOptions;
	}

	@Override
	public void setMarker(Marker m) 
	{	
		mMarker = m;
	}

	@Override
	public Marker getMarker() 
	{
		return mMarker;
	}

	@Override
	public boolean isPublished() {
		return true;
	}

}
