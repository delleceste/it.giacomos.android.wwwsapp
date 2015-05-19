package it.giacomos.android.wwwsapp.layers.installService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStateChangedBroadcastReceiver extends BroadcastReceiver {

	private ServiceStateChangedBroadcastReceiverListener mServiceStateChangedBroadcastReceiverListener;
	
	public ServiceStateChangedBroadcastReceiver(ServiceStateChangedBroadcastReceiverListener li)
	{
		mServiceStateChangedBroadcastReceiverListener = li;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if(intent.hasExtra("serviceStateChanged"))
		{
			String layerName = intent.getStringExtra("layerName");
			InstallTaskState state = (InstallTaskState) intent.getSerializableExtra("serviceStateChanged");
			int percent = intent.getIntExtra("percent", 0);
			mServiceStateChangedBroadcastReceiverListener.onStateChanged(layerName, state, percent);
		}

	}

}
