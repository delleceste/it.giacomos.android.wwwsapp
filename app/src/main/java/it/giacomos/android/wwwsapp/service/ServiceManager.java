package it.giacomos.android.wwwsapp.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ServiceManager 
{	
	public ServiceManager()
	{
	}

	public boolean isServiceRunning(Context ctx)
	{
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
		{
			if (UpdateMyLocationService.class.getName().equals(service.service.getClassName()))
				return true;
		}
		return false;
	}
	
	public boolean setEnabled(Context context, boolean enabled) 
	{
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		Intent myIntent = new Intent(context, UpdateMyLocationService.class);
		NetworkInfo netinfo = connMgr.getActiveNetworkInfo();

		if(enabled && netinfo != null && netinfo.isConnected())
		{
			Log.e(" ServiceManager", "enabled " + enabled + " starting service UpdateMyLocationService");
		    context.startService(myIntent);
			return true;
		}
		else
		{
			if(netinfo != null)
				Log.e("ServiceMan.setEnabled", "enabled "  + enabled +
						" net up " +  connMgr.getActiveNetworkInfo().isConnectedOrConnecting() + " stopping service UpdateMyLocationService");
			else
				Log.e("ServiceMan.setEnabled", "enabled "  + enabled + " net info null: stopping service LayerInstallService");
			return context.stopService(myIntent);
		}
		
		// return false;
	}

}

