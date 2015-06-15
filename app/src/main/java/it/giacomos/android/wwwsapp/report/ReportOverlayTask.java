package it.giacomos.android.wwwsapp.report;

import android.content.Context;
import android.os.AsyncTask;

public class ReportOverlayTask extends AsyncTask<DataInterface, Integer, DataInterface[] > 
{
	private Context mContext;
	private ReportProcessingTaskListener mReportOverlayTaskListener;
	
	public ReportOverlayTask(Context ctx, ReportProcessingTaskListener rotl)
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
		mReportOverlayTaskListener.onReportProcessingTaskFinished(dataI);
	}
}
