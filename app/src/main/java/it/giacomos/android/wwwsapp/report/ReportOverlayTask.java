package it.giacomos.android.wwwsapp.report;

import android.content.Context;
import android.os.AsyncTask;

public class ReportOverlayTask extends AsyncTask<String, Integer, DataInterface[] >
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
	/**
	 * @param data data[0] layer name data[1] downloaded document (JSON)
	 */
	protected DataInterface[] doInBackground(String... data)
	{
		if(data == null)
			return null;

		/* ok start processing data */
		DataParser reportDataFactory = new DataParser();
		DataInterface dataList[] = reportDataFactory.parse(data[1]);

		int dataSiz = dataList.length;
		for(int i = 0; i < dataSiz; i++)
		{
			if(this.isCancelled())
				break;
			
			DataInterface dataInterface = dataList[i];
			dataInterface.buildMarkerOptions(mContext);
		}
		return dataList;
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
