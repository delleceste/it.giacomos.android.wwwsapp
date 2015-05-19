package it.giacomos.android.wwwsapp.network;

public enum DownloadReason {
	/** download was left incomplete */
	Incomplete, 
	/** no data has been downloaded */
	Init, 
	/** data is old and has to be refreshed */
	DataExpired,
	/** just a part of the data is being downloaded */
	PartialDownload;
}
