/**
 * 
 */
package it.giacomos.android.wwwsapp.layers;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author giacomo
 *
 */
public class LayerListDownloadService extends Service implements LayerFetchTaskListener {

	private String mErrorMsg;
	private String mAppLang;
	private int mAppVersionCode;
	private LayerListDownloadServiceState mState;
	private LayerFetchTask mLayerFetchTask;

	/**
	 * @param name
	 */
	public LayerListDownloadService() {
		super();
		mErrorMsg = "";
	}

	/** If wi fi network is enabled, I noticed that turning on 3G network as well 
	 * produces this method to be invoked another times. That is, the ConnectivityChangedReceiver
	 * triggers a Service start command. In this case, we must avoid that the handler schedules
	 * another execution of the timer.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(intent != null && intent.hasExtra("download"))
		{
			mAppVersionCode = intent.getIntExtra("version", 0);
			mAppLang = intent.getStringExtra("lang");
			mDownloadLayersList();
		}
		else if(intent != null && intent.hasExtra("cancel"))
			mCancelDownload();
		
		return Service.START_STICKY;
	}

	private void mCancelDownload()
	{
		if(mLayerFetchTask != null && mLayerFetchTask.getStatus() != AsyncTask.Status.FINISHED)
			mLayerFetchTask.cancel(true);
		/* state cancelled will be set by onLayerFetchCancelled */
	}
	
	private void mDownloadLayersList()
	{
		mState = LayerListDownloadServiceState.DOWNLOADING;
		if(mLayerFetchTask != null && mLayerFetchTask.getStatus() != AsyncTask.Status.FINISHED)
			mLayerFetchTask.cancel(true);
		mLayerFetchTask = new LayerFetchTask(this, mAppVersionCode, mAppLang, this);
		mLayerFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	private void mNotifyStateChanged(String layer, float version, int percent)
	{
		Intent stateChangedNotif = new Intent(LayerListActivity.LIST_DOWNLOAD_SERVICE_STATE_CHANGED_INTENT);
		stateChangedNotif.putExtra("listDownloadServiceState", mState);
		stateChangedNotif.putExtra("percent", percent);
		stateChangedNotif.putExtra("error", mErrorMsg);
		if(layer.length() > 0)
		{
			stateChangedNotif.putExtra("layerName", layer);
			stateChangedNotif.putExtra("version", version);
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(stateChangedNotif);		
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLayersUpdated(boolean success, String errorMessage) 
	{
		if(!success)
		{
			mErrorMsg = errorMessage;
			mState = LayerListDownloadServiceState.ERROR;
		}
		mState = LayerListDownloadServiceState.COMPLETE;
		mNotifyStateChanged("", -1, 100);	
	}

	@Override
	public void onLayerFetchProgress(LayerFetchTaskProgressData d) 
	{
		mState = LayerListDownloadServiceState.DOWNLOADING;
		Log.e("LayerListDownloadService.onLayerFetchProgress", " percent " + d.percent);
		mNotifyStateChanged(d.name, d.available_version, d.percent);
	}

	@Override
	public void onLayerFetchCancelled() 
	{
		mState = LayerListDownloadServiceState.CANCELLED;
		mNotifyStateChanged("", -1, 100);
		
	}
	
}
