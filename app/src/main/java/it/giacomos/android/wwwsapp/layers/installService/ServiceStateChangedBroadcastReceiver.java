package it.giacomos.android.wwwsapp.layers.installService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceStateChangedBroadcastReceiver extends BroadcastReceiver {

	private ServiceStateChangedBroadcastReceiverListener mServiceStateChangedBroadcastReceiverListener;
	
	public ServiceStateChangedBroadcastReceiver()
	{
		
	}
	
	public void registerListener(ServiceStateChangedBroadcastReceiverListener li)
	{
		mServiceStateChangedBroadcastReceiverListener = li;
	}
	
	public void unregisterListener()
	{
		mServiceStateChangedBroadcastReceiverListener = null;
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if(mServiceStateChangedBroadcastReceiverListener != null && intent.hasExtra("serviceStateChanged"))
		{
			String layerName = intent.getStringExtra("layerName");
			String stateName = intent.getStringExtra("serviceStateChanged");
			InstallTaskState state = InstallTaskState.valueOf(stateName);
			int percent = intent.getIntExtra("percent", 0);
			mServiceStateChangedBroadcastReceiverListener.onInstallServiceStateChanged(layerName, state, percent);
		}
	}
}
