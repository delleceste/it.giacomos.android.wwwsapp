package it.giacomos.android.wwwsapp.widgets.map.report;

import it.giacomos.android.wwwsapp.R;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportData extends DataInterface
{
	public String username, locality, comment, temperature, writable;
	public int sky, wind;
	
	private Marker mMarker;
	private MarkerOptions mMarkerOptions;
	
	public ReportData(String u, String d, String l, String c, 
			String t, int s, int w, double lat, double longi, String writa)
	{
		super(lat, longi, d);
		username = u;
		locality = l;
		comment = c;
		temperature = t;
		sky = s;
		wind = w;
		writable = writa;
		mMarker = null;
	}

	public boolean isWritable()
	{
		return (writable.compareTo("w") == 0);
	}

	@Override
	public int getType() 
	{
		return DataInterface.TYPE_REPORT;
	}

	@Override
	public String getLocality() {
		return locality;
	}

	@Override
	public MarkerOptions buildMarkerOptions(Context ctx) 
	{
		String text = "";
		String title = "";
		String skystr = "";
		int windIdx, iconId  = -1;
		boolean night = false;
		
		String windtxtItems[] = ctx.getResources().getStringArray(R.array.report_wind_textitems);
		mMarkerOptions = null;
		Resources res = ctx.getResources();

		if(sky == 1)
		{
//			if(night)
//				iconId = R.drawable.weather_sky_0_nite;
//			else
//				iconId = R.drawable.weather_sky_0;
//			
//			skystr += res.getString(R.string.sky0);
		}
		title = username;
		if(locality.length() > 1) /* different from "-" */
			title += " - " + locality;
		
		text = getDateTime() + "\n";
		
		if(skystr.length() > 0)
			text += skystr + "\n";
		
		if(wind > 0)
			text += res.getString(R.string.wind) + ": " + windtxtItems[wind] + "\n";
		
		try{
			Float.parseFloat(temperature);
			text += res.getString(R.string.temp)  + ": " + temperature + "C\n";
			
		}
		catch(NumberFormatException e)
		{
			
		}
		if(comment.length() > 0)
			text += res.getString(R.string.reportComment) + ":\n" + comment;
		
		if(isWritable())
			text += "\n*" + res.getString(R.string.reportTouchBaloonToRemove);
		
		mMarkerOptions = new MarkerOptions();
		mMarkerOptions.position(new LatLng(getLatitude(), getLongitude()));
		mMarkerOptions.title(title);
		mMarkerOptions.snippet(text);

		if(iconId > -1)
		{
			/* for sky no label, so do not use obsBmpFactory */
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconId);
			if(bitmapDescriptor != null)
			{
				mMarkerOptions.icon(bitmapDescriptor);
				mMarkerOptions.anchor(0.5f, 0.5f);
			}
		}
		else
		{
			windIdx = wind;
			if(windIdx == 1)
//				iconId = R.drawable.weather_wind_calm;
			
			if(iconId > -1)
			{
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconId);
				if(bitmapDescriptor != null)
					mMarkerOptions.icon(bitmapDescriptor);
			}
			else
				mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		}
		
		return mMarkerOptions;

	}

	public void setMarker(Marker m)
	{
		mMarker = m;
	}
	
	public MarkerOptions getMarkerOptions()
	{
		return mMarkerOptions;
	}
	
	@Override
	public Marker getMarker() 
	{
		return mMarker;
	}

	/** @return true: always return true for reports.
	 * 
	 */
	@Override
	public boolean isPublished() {
		return true;
	}

	
	
}
