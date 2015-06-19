package it.giacomos.android.wwwsapp.report;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportOverlayTask extends AsyncTask<String, Integer, HashMap<String , DataInterface> >
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
	protected HashMap<String , DataInterface> doInBackground(String... data)
	{
		if(data == null)
			return null;

		String layerName = data[0];
		/* ok start processing data.
		 * DataParser creates DataInterface objects and creates markers, using the
		 * XmlUIDocumentRepr to select the relevant fields
		 */
		DataParser reportDataFactory = new DataParser();
		HashMap<String, DataInterface> dataList = reportDataFactory.parse(layerName, data[1], mContext);
		return dataList;
	}
	
	@Override
	public void onCancelled(HashMap<String , DataInterface>dataI)
	{
		
	}
	
	@Override
	public void onPostExecute(HashMap<String , DataInterface> dataI)
	{
		mReportOverlayTaskListener.onReportProcessingTaskFinished(dataI);
	}
}
