package it.giacomos.android.wwwsapp.layers;

public interface LayerFetchTaskListener {
	
	public void onLayersUpdated(boolean success, String errorMessage);
	
	public void onLayerFetchCancelled();

	void onLayerFetchProgress(LayerFetchTaskProgressData d);
	
	
}
