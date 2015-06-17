package it.giacomos.android.wwwsapp.report.network;

import com.google.android.gms.maps.model.LatLngBounds;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.wwwsapp.layers.FileUtils;
import it.giacomos.android.wwwsapp.network.NetworkStatusMonitor;
import it.giacomos.android.wwwsapp.network.NetworkStatusMonitorListener;
import it.giacomos.android.wwwsapp.network.Urls;

public class ReportUpdater   
implements NetworkStatusMonitorListener, ReportUpdateTaskListener
{
	private final String REPORT_URL = "http://www.giacomos.it/wwwsapp/get_report.php";
	private static final long DOWNLOAD_REPORT_OLD_TIMEOUT = 10000;

	private Context mContext;
	private ReportDownloadListener mReportDownloadListener;
	private NetworkStatusMonitor mNetworkStatusMonitor;
	private long mLastReportUpdatedAt;
	private ReportUpdateTask mReportUpdateTask;
	private LatLngBounds mNewArea, mCurrentArea;
	private String mNewLayerName, mCurrentLayerName, mAccount;

	public ReportUpdater(Context ctx, String account)
	{
		mContext = ctx;
		mNetworkStatusMonitor = new NetworkStatusMonitor(this, ctx);
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mLastReportUpdatedAt = 0;
		mReportUpdateTask = null;
		mCurrentLayerName = mNewLayerName = "";
        mAccount = account;
	}

	public void unregister()
	{
		/* when the activity is paused, disconnect from network status monitor and 
		 * from location client. We can also cancel the report update task, since 
		 * when the activity is resumed an update is performed.
		 */
		// Log.e("ReportUpdater.unregister", "calling clear()");
		mReportDownloadListener = null;
		clear();
	}

	public void register(ReportDownloadListener rul)
	{
		// Log.e("register", "registering network status monitor in register");
		/* must (re)connect with the network status monitor in order to be notified when the network 
		 * goes up or down
		 */
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mReportDownloadListener = rul;
	}

	public void clear()
	{
		Log.e("ReportUpdater.clear()", "unregistering network status monitor receiver and location client. Cancelling tasks");
		try
		{
			mContext.unregisterReceiver(mNetworkStatusMonitor);
		}
		catch(IllegalArgumentException iae)
		{
			/* when the activity is destroyed, unregister calls clear and then onDestroy in map fragment
			 * calls clear() again. On the other side, we need to call clear even if only paused and 
			 * not destroyed.
			 */
		}
		/* cancel thread if running */
		if(mReportUpdateTask != null)
			mReportUpdateTask.cancel(false);
	}

	public void setLayer(String layer)
	{
		mNewLayerName = layer;
		update(mNewLayerName.compareTo(mCurrentLayerName) != 0);
	}

	public void update(boolean force)
	{
        Log.e("RepUpdater.update", "force " + force + " up2 date " + reportUpToDate() + " connected " + mNetworkStatusMonitor.isConnected()
            + " mNewArea: " + mNewArea);
		if((!reportUpToDate() || force) && mNewArea != null && mNetworkStatusMonitor.isConnected())
		{
			/* cancel previous task if running and if it is not processing the same area as we are going to process */
			Log.e("ReportUpd.update", " Updating reports: force " + force + " latlng changed " +
					(mCurrentArea != mNewArea) + " b1 " + mCurrentArea + " b2 " + mNewArea);
			if (mReportUpdateTask != null && !mReportUpdateTask.isProcessingArea(mNewArea) && mReportUpdateTask.getStatus() != AsyncTask.Status.FINISHED)
				mReportUpdateTask.cancel(false);
			else if(mReportUpdateTask == null || !mReportUpdateTask.isProcessingArea(mNewArea))
			{
				/* create and start a new task only if the interesting area is not currently being processed by a running task */
				Log.e("ReportUpd.update", "creating new task for area " + mNewArea);
				String [] task_data = new String[3];
				task_data[0] = REPORT_URL;
				task_data[1] = mNewLayerName;
                task_data[2] = mAccount;
				mReportUpdateTask = new ReportUpdateTask(this, mNewArea);
				mReportUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, task_data);
			}
			else if(mReportUpdateTask != null)
				Log.e("ReportUpd.update", "update: is processing area: " + mReportUpdateTask.isProcessingArea(mNewArea) );
		}
	}

	@Override
	public void onNetworkBecomesAvailable() 
	{
		/* force an update if map visible area bounds changed
		 * Otherwise, update only if some time has elapsed after last update.
		 */
		update( (mNewArea != mCurrentArea) || (mCurrentLayerName.compareTo(mNewLayerName) != 0));
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{

	}

	/** Evaluate if the report is old 
	 * 
	 * @return
	 */
	public boolean reportUpToDate() 
	{	
		return (System.currentTimeMillis() - mLastReportUpdatedAt) < DOWNLOAD_REPORT_OLD_TIMEOUT;
	}

	@Override
	public void onReportUpdateTaskComplete(boolean error, String[] data)
	{
		if(!error)
		{
			/* call onReportDownloaded on ReportOverlay */
			if(mReportDownloadListener != null)
				mReportDownloadListener.onReportDownloaded(data);

			mCurrentArea = mNewArea;
			mCurrentLayerName = mNewLayerName;
			mLastReportUpdatedAt = System.currentTimeMillis();
		}
		else if(mReportDownloadListener != null)
			mReportDownloadListener.onReportDownloadError(mReportUpdateTask.getError());
	}

	public void areaChanged(LatLngBounds bounds)
	{
        if(mNewArea == null ||
                bounds.northeast.latitude != mNewArea.northeast.latitude ||
                bounds.northeast.longitude != mNewArea.northeast.longitude ||
                bounds.southwest.latitude != mNewArea.southwest.latitude ||
                bounds.southwest.longitude != mNewArea.southwest.longitude)
        {
            Log.e("ReportUpd.areaChanged", "area changed from " + mNewArea + " to " + bounds);
            mNewArea = bounds;
            if (mNetworkStatusMonitor.isConnected())
                update(true); /* true: on area change, force update */
        }
	}

}
