package it.giacomos.android.wwwsapp.layers;

public interface LayerListServiceStateChangedBroadcastReceiverListener 
{
	public void onLayerListServiceStateChanged(String layerName, float version, LayerListDownloadServiceState s, int percent, String error);
}
