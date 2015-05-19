package it.giacomos.android.wwwsapp.widgets.map.report;

import android.content.Context;
import android.os.AsyncTask;

public class ReportOverlayTask extends AsyncTask<DataInterface, Integer, DataInterface[] > 
{
	private Context mContext;
	private ReportOverlayTaskListener mReportOverlayTaskListener;
	
	public ReportOverlayTask(Context ctx, ReportOverlayTaskListener rotl)
	{
		super();
		mContext = ctx;
		mReportOverlayTaskListener = rotl;
	}
	
	@Override
	protected DataInterface[] doInBackground(DataInterface... params) 
	{
		if(params == null)
			return null;
		
		int dataSiz = params.length;
		for(int i = 0; i < dataSiz; i++)
		{
			if(this.isCancelled())
				break;
			
			DataInterface dataInterface = params[i];
			dataInterface.buildMarkerOptions(mContext);
		}
		return params;
	}
	
	@Override
	public void onCancelled(DataInterface [] dataI)
	{
		
	}
	
	@Override
	public void onPostExecute(DataInterface [] dataI)
	{
		mReportOverlayTaskListener.onReportOverlayTaskFinished(dataI);
	}
}
