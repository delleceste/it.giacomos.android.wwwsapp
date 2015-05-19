package it.giacomos.android.wwwsapp.interfaceHelpers;
import android.content.res.Resources;
import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.DownloadStatus;
import it.giacomos.android.wwwsapp.network.state.ViewType;
import it.giacomos.android.wwwsapp.widgets.map.MapMode;
import it.giacomos.android.wwwsapp.widgets.map.MapViewMode;
import it.giacomos.android.wwwsapp.widgets.map.OMapFragment;

import java.lang.String;

public class TitleBuilder 
{
	public String makeTitle(HelloWorldActivity a)
	{
		DownloadStatus ds = a.getDownloadStatus();
		boolean networkAvailable = ds.isOnline;
		ViewType vt = a.getCurrentViewType();
		Resources res = a.getResources();
		String t = "";
		
		return t;
	}
}
