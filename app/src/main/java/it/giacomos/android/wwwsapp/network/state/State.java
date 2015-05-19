package it.giacomos.android.wwwsapp.network.state;

import java.net.URL;

import it.giacomos.android.wwwsapp.network.DownloadManagerUpdateListener;
import it.giacomos.android.wwwsapp.network.DownloadStatus;
import it.giacomos.android.wwwsapp.widgets.map.MapMode;

public abstract class State
{
	protected final DownloadStatus dDownloadStatus;
	
	public State(DownloadManagerUpdateListener l, DownloadStatus downloadStatus)
	{
		m_downloadManagerUpdateListener = l;
		dDownloadStatus = downloadStatus;
	}	

	public DownloadStatus getDownloadStatus()
	{
		return dDownloadStatus;
	}
	
	public void getSituation()
	{
		
	}

	public void getTodayForecast()
	{
		
	}

	public void getTomorrowForecast()
	{
		
	}

	public void getTwoDaysForecast()
	{
		
	}

	public void getThreeDaysForecast()
	{
		
	}

	public void getFourDaysForecast()
	{
		
	}

	public void getObservationsTable(MapMode mapMode) 
	{
		
	}
	
	public abstract StateName name();

	protected DownloadManagerUpdateListener m_downloadManagerUpdateListener;

	public void getRadarImage() {
		
		
	}
	
	public void getWebcamList() 
	{
		
	}

	public void getReport(String url) 
	{
		
	}

}
