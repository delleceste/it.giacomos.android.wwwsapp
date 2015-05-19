package it.giacomos.android.wwwsapp.layers.installService;

public interface ServiceStateChangedBroadcastReceiverListener 
{
	public void onStateChanged(String layerName, InstallTaskState s, int percent);
}
