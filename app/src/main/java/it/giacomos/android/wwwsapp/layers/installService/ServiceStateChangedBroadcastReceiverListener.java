package it.giacomos.android.wwwsapp.layers.installService;

public interface ServiceStateChangedBroadcastReceiverListener 
{
	public void onInstallServiceStateChanged(String layerName, InstallTaskState s, int percent);
}
