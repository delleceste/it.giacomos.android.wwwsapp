package it.giacomos.android.wwwsapp.widgets.map.report.network;

public interface ReportUpdaterListener 
{
	public void onReportUpdateDone(String doc);
	
	public void onReportUpdateError(String error);

	public void onReportUpdateMessage(String message);
}
