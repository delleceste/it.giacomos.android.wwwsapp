package it.giacomos.android.wwwsapp.report.network;

public interface ReportDownloadListener
{
	public void onReportDownloaded(String doc);
	
	public void onReportDownloadError(String error);

	public void onReportUpdateMessage(String message);

	void onReportDownloadStarted();
}
