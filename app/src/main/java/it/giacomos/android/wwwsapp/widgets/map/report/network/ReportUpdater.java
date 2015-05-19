package it.giacomos.android.wwwsapp.widgets.map.report.network;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.*;

import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.FileUtils;
import it.giacomos.android.wwwsapp.network.NetworkStatusMonitor;
import it.giacomos.android.wwwsapp.network.NetworkStatusMonitorListener;
import it.giacomos.android.wwwsapp.network.state.Urls;
import it.giacomos.android.wwwsapp.network.state.ViewType;

public class ReportUpdater   
implements NetworkStatusMonitorListener,
ReportUpdateTaskListener, ConnectionCallbacks, OnConnectionFailedListener
{
	private static final long DOWNLOAD_REPORT_OLD_TIMEOUT = 10000;

	private Context mContext;
	private ReportUpdaterListener mReportUpdaterListener;
	private GoogleApiClient mGoogleApiClient;
	private NetworkStatusMonitor mNetworkStatusMonitor;
	private long mLastReportUpdatedAt;
	private ReportUpdateTask mReportUpdateTask;

	public ReportUpdater(Context ctx, ReportUpdaterListener rul)
	{
		mContext = ctx;
		mGoogleApiClient = new GoogleApiClient.Builder(ctx).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
		mNetworkStatusMonitor = new NetworkStatusMonitor(this);
		/* when the map switches mode, a new ReportUpdater is created, and it must be registered.
		 * onResume is not called when map switches mode. Instead, when the activity is paused, 
		 * the onPause method of ReportUpdater unregisters from NetworkStatusMonitor.
		 */
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mReportUpdaterListener = rul;
		mLastReportUpdatedAt = 0;
		mReportUpdateTask = null;
		FileUtils dpcu = new FileUtils();
	}


	public void onPause() 
	{
		/* when the activity is paused, disconnect from network status monitor and 
		 * from location client. We can also cancel the report update task, since 
		 * when the activity is resumed an update is performed.
		 */
		// Log.e("ReportUpdater.onPause", "calling clear()");
		clear();
	}

	public void onResume()
	{
		// Log.e("onResume", "registering network status monitor in onResume");
		/* must (re)connect with the network status monitor in order to be notified when the network 
		 * goes up or down
		 */
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public void clear()
	{
		Log.e("ReportUpdater.clear()", "unregistering network status monitor receiver and location client");
		try
		{
			mContext.unregisterReceiver(mNetworkStatusMonitor);
		}
		catch(IllegalArgumentException iae)
		{
			/* when the activity is destroyed, onPause calls clear and then onDestroy in map fragment
			 * calls clear() again. On the other side, we need to call clear even if only paused and 
			 * not destroyed.
			 */
		}
		mGoogleApiClient.disconnect();
		/* cancel thread if running */
		if(mReportUpdateTask != null)
			mReportUpdateTask.cancel(false);
	}

	public void update(boolean force)
	{
		if((System.currentTimeMillis() - mLastReportUpdatedAt > DOWNLOAD_REPORT_OLD_TIMEOUT) || force)
		{
			/* if a task is already running or about to run, do not do anything, because an update is on
			 * the way.
			 */
			if(mReportUpdateTask != null && (mReportUpdateTask.getStatus() == AsyncTask.Status.PENDING 
					|| mReportUpdateTask.getStatus() == AsyncTask.Status.RUNNING))
			{
				Log.e("update", "reportUpdateTask is running or pending");
				return;
			}

			if(mNetworkStatusMonitor.isConnected())
			{
				mGoogleApiClient.disconnect();
				mGoogleApiClient.connect();
			}
			else /* offline */
				Toast.makeText(mContext, R.string.reportNeedToBeOnline, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNetworkBecomesAvailable() 
	{
		if(mGoogleApiClient.isConnected())
			onConnected(null);
		else if(mGoogleApiClient.isConnecting())
			return; /* wait for onConnected() */
		else
			mGoogleApiClient.connect();
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) 
	{
		Toast.makeText(mContext, "ReportUpdater: failed to connect to "
				+ "location service: " + arg0.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	/** Called when the location client is connected. It is possible to obtain the last
	 * known location. So we start here the report update task to download the reports.
	 */
	public void onConnected(Bundle arg0) 
	{
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) ;
		/* since 2.20, allow fetching the reports even with geolocation disabled. Put latitude and
		 * longitude to 0.0
		 */
		if(location == null)
		{
			location = new Location("DummyLocation");
			location.setLatitude(0.0);
			location.setLongitude(0.0);
			mReportUpdaterListener.onReportUpdateMessage(mContext.getString(R.string.enable_location_for_full_functionality));
		}
		/* if a task is already running or about to run, do not do anything, because an update is on
		 * the way.
		 */
		if(mReportUpdateTask != null && (mReportUpdateTask.getStatus() == AsyncTask.Status.PENDING 
				|| mReportUpdateTask.getStatus() == AsyncTask.Status.RUNNING))
			return;

		String deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

		if(mReportUpdateTask != null && mReportUpdateTask.getStatus() != AsyncTask.Status.FINISHED)
			mReportUpdateTask.cancel(false);

		mReportUpdateTask = new ReportUpdateTask(this, location, deviceId);
		/* "http://www.giacomos.it/meteo.fvg/get_report_2_6_1.php" */
		mReportUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().getReportUrl());


		/* no more interested in location updates, the task has been starting with the last known
		 * location.
		 */
		mGoogleApiClient.disconnect();
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
	public void onReportUpdateTaskComplete(boolean error, String data) 
	{
		if(!error)
		{
			/* call onReportUpdateDone on ReportOverlay */
			mReportUpdaterListener.onReportUpdateDone(data);
			//			Log.e("ReportUpdater.onPostExecute", "saving to cache: " + data);
			/* save data into cache */
			FileUtils dataPoolCUtils = new FileUtils();
			/// dataPoolCUtils.saveToStorage(data.getBytes(), ViewType.REPORT, mContext);
			mLastReportUpdatedAt = System.currentTimeMillis();
		}
		else
			mReportUpdaterListener.onReportUpdateError(mReportUpdateTask.getError());
	}


	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

	}




}
