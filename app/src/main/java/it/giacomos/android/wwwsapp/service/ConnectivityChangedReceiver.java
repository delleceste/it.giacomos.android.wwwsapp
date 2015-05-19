package it.giacomos.android.wwwsapp.service;

import it.giacomos.android.wwwsapp.preferences.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ConnectivityChangedReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{  
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
		
		
//		if(netinfo!= null)
//		{
//			Log.e("ConnectivityChangedReceiver.onReceive", "connecting or connected " + netinfo.isConnectedOrConnecting());
//			Log.e("ConnectivityChangedReceiver.onReceive", "connected " + netinfo.isConnected());
//			Log.e("ConnectivityChangedReceiver.onReceive", "isAvailable " + netinfo.isAvailable());
//			Log.e("ConnectivityChangedReceiver.onReceive", "network info type " + netinfo.getState());
//		}
//		else
//			Log.e("ConnectivityChangedReceiver.onReceive", "net info null ");
		
		Settings s = new Settings(context);
		boolean notificationServiceEnabled = s.notificationServiceEnabled();
		if(notificationServiceEnabled)
			new ServiceManager().setEnabled(context, true);
		else
			Log.e("ConnectivityChangedReceiver.onReceive", "not starting service. (disabled)");
		
		/* rain alert. Start radar image sync and image comparison */
		if(s.rainNotificationEnabled() && netinfo != null && netinfo.isConnected())
		{
			Log.e("ConnectivityChangedReceiver", "starting radarSyncRainDetectIntentService");
		}
		else
			Log.e("ConnectivityChangedReceiver", "not starting radarSyncRainDetectIntentService " + netinfo);
	}
}
