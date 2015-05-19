package it.giacomos.android.wwwsapp.network.Data;

import it.giacomos.android.wwwsapp.network.state.BitmapType;
import it.giacomos.android.wwwsapp.network.state.ViewType;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostType;

public interface DataPoolErrorListener {
	
	public abstract void onBitmapUpdateError(BitmapType t, String error);
	
	public abstract void onTextUpdateError(ViewType t, String error);
}
