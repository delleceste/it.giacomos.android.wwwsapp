package it.giacomos.android.wwwsapp.network.state;

import it.giacomos.android.wwwsapp.network.DownloadManagerUpdateListener;
import it.giacomos.android.wwwsapp.network.DownloadStatus;

public class Offline extends State {

	public Offline(DownloadManagerUpdateListener l, DownloadStatus ds) {
		super(l, ds);
		dDownloadStatus.isOnline = false;
	}

	public StateName name() { return StateName.Offline; }
}
