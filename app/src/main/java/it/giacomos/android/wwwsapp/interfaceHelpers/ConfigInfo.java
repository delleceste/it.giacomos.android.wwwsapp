package it.giacomos.android.wwwsapp.interfaceHelpers;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.locationUtils.LocationService;
import it.giacomos.android.wwwsapp.network.DownloadStatus;

import java.util.Date;

import android.app.Activity;
import android.location.Location;

import java.text.DateFormat;
public class ConfigInfo {

	public String gatherInfo(Activity mActivity) 
	{
		String info = "";
		LocationService ls = (LocationService) ((HelloWorldActivity) mActivity).getLocationService();

		info += "<h5>Location service</h5>";
		info += "<p>";
		
		info += "<strong>Current location available</strong>:<br/>";
		Location location = ls.getCurrentLocation();
		if(location == null)
			info += "- no<br/>";
		else
		{
			info += "- yes<br/>";
			info += "- current provider: " + location.getProvider() + "<br/>";
			info += "- accuracy: " + location.getAccuracy() + "m <br/>";
			Date date = new Date(location.getTime());
			DateFormat df = DateFormat.getDateTimeInstance();
			String time = df.format(date);
			info += "- last loc. timestamp: " + time + " <br/>";
		}
		info += "<br/>Using Google Play Services Location client";
		info += "</p>";
		
		info += "<h5>Data update policy</h5>";
		info += "<p>" +
				"Automatic: on network available <strong>or</strong> <cite>onResume</cite> <strong>and</strong> after at least " +
				DownloadStatus.DOWNLOAD_OLD_TIMEOUT + "ms from previous successful update; <br/>" +
				"" +
				"</p>";
		
		info += "<h5>Data retrieval strategy</h5>";
		info += "<p>" +
				"<strong>Multi threaded</strong>, parallel download achieved with <strong>AsyncTask</strong>s.<br/> " +
				"THREAD_POOL_EXECUTOR Executor on Android build version > HONEYCOMB;" +
				"<br/>Last downloaded data is saved on storage for <em>offline</em> viewing." +
				"</p>";
		
		info += "<h5>Google Maps API</h5>";
		info += "<p>" +
				"Using Google Maps API v2" +
				"</p>";
		
		return info;
	}

}
