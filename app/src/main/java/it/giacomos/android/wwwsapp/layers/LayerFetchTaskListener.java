package it.giacomos.android.wwwsapp.layers;

import java.util.ArrayList;

public interface LayerFetchTaskListener {
	
	public void onLayersUpdated(ArrayList<LayerItemData> data, String errorMessage);
	
	public void onLayerFetchProgress(int progress, int total);

	public void onLayerFetchCancelled(int size, int mTotal);
	
	
}
