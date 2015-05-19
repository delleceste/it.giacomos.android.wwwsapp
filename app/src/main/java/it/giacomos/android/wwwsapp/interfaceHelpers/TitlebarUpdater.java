package it.giacomos.android.wwwsapp.interfaceHelpers;

import android.support.v7.widget.Toolbar;
import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.DownloadStatus;

public class TitlebarUpdater {
	public void update(HelloWorldActivity a)
	{
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		a.getSupportActionBar().setTitle(a.getTitle());
		Toolbar toolb = (Toolbar) a.findViewById(R.id.toolbar);
		toolb.setTitle(a.getTitle());
		titleBuilder = null;
	}
}
