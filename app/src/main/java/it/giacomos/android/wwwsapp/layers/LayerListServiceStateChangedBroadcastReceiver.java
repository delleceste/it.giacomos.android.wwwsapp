package it.giacomos.android.wwwsapp.layers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LayerListServiceStateChangedBroadcastReceiver extends BroadcastReceiver {

	private LayerListServiceStateChangedBroadcastReceiverListener mServiceStateChangedBroadcastReceiverListener;
	
	public LayerListServiceStateChangedBroadcastReceiver()
	{
		
	}
	
	public void registerListener(LayerListServiceStateChangedBroadcastReceiverListener li)
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
		if(mServiceStateChangedBroadcastReceiverListener != null && intent.hasExtra("listDownloadServiceState"))
		{
			String layerName = intent.getStringExtra("layerName");
			LayerListDownloadServiceState state = (LayerListDownloadServiceState) intent.getSerializableExtra("listDownloadServiceState");
			int percent = intent.getIntExtra("percent", 0);
			float version = intent.getFloatExtra("version", -1.0f);
			String error = intent.getStringExtra("error");
			mServiceStateChangedBroadcastReceiverListener.onStateChanged(layerName,
						version, state, percent, error);
		}
	}
}
