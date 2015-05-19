package it.giacomos.android.wwwsapp.layers;

public interface DownloadManagerStatusWatcherListener 
{
	public void onDownloadStatusUpdate(int downloadId, 
			String message,
			int downloadStatusCode,
			double completed_percent);

}
