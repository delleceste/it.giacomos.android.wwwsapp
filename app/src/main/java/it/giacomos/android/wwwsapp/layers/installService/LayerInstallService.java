package it.giacomos.android.wwwsapp.layers.installService;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.gcm.GcmRegistrationManager;
import it.giacomos.android.wwwsapp.layers.LayerListActivity;
import it.giacomos.android.wwwsapp.network.state.Urls;
import it.giacomos.android.wwwsapp.preferences.Settings;
import it.giacomos.android.wwwsapp.service.sharedData.NotificationData;
import it.giacomos.android.wwwsapp.service.sharedData.NotificationDataFactory;
import it.giacomos.android.wwwsapp.service.sharedData.RainNotification;
import it.giacomos.android.wwwsapp.service.sharedData.ServiceSharedData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class LayerInstallService extends Service implements InstallTaskListener
{
	private HashMap<String, InstallTask> mInstallTasks;
	private HashMap<String, Integer> mNotificationIdMap;
	private int mNotificationIdx;

	public LayerInstallService() 
	{
		super();
		mInstallTasks = new HashMap<String, InstallTask>();
		mNotificationIdMap = new HashMap<String, Integer>();
		mNotificationIdx = 1;
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}

	/** If wi fi network is enabled, I noticed that turning on 3G network as well 
	 * produces this method to be invoked another times. That is, the ConnectivityChangedReceiver
	 * triggers a Service start command. In this case, we must avoid that the handler schedules
	 * another execution of the timer.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(intent != null)
		{
			if(intent.hasExtra("downloadLayer"))
				addLayer(intent.getStringExtra("downloadLayer"));
			else if(intent.hasExtra("cancelDownload"))
				cancelInstall(intent.getStringExtra("cancelDownload"));
		}
		return Service.START_STICKY;
	}

	private void cancelInstall(String layerName) {
		
		if(mInstallTasks.containsKey(layerName))
		{
			InstallTask task = mInstallTasks.get(layerName);
			if(task.getStatus() != AsyncTask.Status.FINISHED)
				task.cancel(true);
			mInstallTasks.remove(layerName);
		}
	}

	public synchronized void addLayer(String name)
	{
		startTask(name);
	}
	
	private void startTask(String layerName)
	{
		/* check that the network is still available */
		final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
		if(netinfo != null && netinfo.isConnected())
		{
			Log.e("LayerInstallService.startTask", "starting task to download layer " + layerName);
			if(mInstallTasks.containsKey(layerName))
			{
				InstallTask task = mInstallTasks.get(layerName);
				if(task.getStatus() == AsyncTask.Status.RUNNING)
					Log.e("LayerInstallService.startTask", "A task installing " + layerName + " is already running!");
			}
			else
			{
				InstallTask task = new InstallTask(this, this, layerName);
				mNotificationIdMap.put(layerName, mNotificationIdx++);
				mInstallTasks.put(layerName, task);
				task.execute();
				mUpdateNotification(layerName, InstallTaskState.DOWNLOADING, 0);
			}
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		for(InstallTask task : mInstallTasks.values())
			task.cancel(true);
		mInstallTasks.clear();
	}

	private void log(String message)
	{
		File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
			out.append(Calendar.getInstance().getTime().toLocaleString()+ ": " + message + "\n");
			out.close();
		} catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInstallTaskProgress(String layerName, int percent) {
		// TODO Auto-generated method stub
		Log.e("LayerInstallService.onInstallTaskProgress", " downloading " + layerName + ": " + percent + "%");
		mNotifyStateChanged(layerName, InstallTaskState.DOWNLOADING, percent);
	}

	@Override
	public void onInstallTaskCancelled(String layerName) {
		// TODO Auto-generated method stub
		Log.e("LayerInstallService.onInstallTaskCancelled", " cancelled download of " + layerName);
		mNotifyStateChanged(layerName, InstallTaskState.DOWNLOAD_CANCELLED, 100);
		mInstallTasks.remove(layerName);
	}

	@Override
	public void onInstallTaskCompleted(String layerName, String errorMessage) {
		Log.e("LayerInstallService.onInstallTaskCompleted", " download of " + layerName + " finished. Errors: " + errorMessage);
		if(errorMessage.isEmpty())
			mNotifyStateChanged(layerName, InstallTaskState.INSTALL_COMPLETE, 100);
		else
			mNotifyStateChanged(layerName, InstallTaskState.INSTALL_ERROR, 100);
		mInstallTasks.remove(layerName);
		mNotificationIdMap.remove(layerName); /* after notification */
	}
	
	private void mNotifyStateChanged(String layerName, InstallTaskState state, int percent)
	{
//		Log.e("GreenDisplayService.mNotifyTutorialActivityStateChanged", " notifying state changed to " + mState.getType());
		Intent stateChangedNotif = new Intent(LayerListActivity.SERVICE_STATE_CHANGED_INTENT);
		stateChangedNotif.putExtra("serviceStateChanged", state);
		stateChangedNotif.putExtra("percent", percent);
		stateChangedNotif.putExtra("layerName", layerName);
		LocalBroadcastManager.getInstance(this).sendBroadcast(stateChangedNotif);		
		/* notification progress */
		mUpdateNotification(layerName, state, percent);
	}
	
	public void checkIfCanStop()
	{
		if(mInstallTasks.size() == 0)
		{
			Log.e("LayerInstallService.checkIfCanStop", "can stop!");
			this.stopSelf();
		}
		else
			Log.e("LayerInstallService.checkIfCanStop", "cant stop remain tasks " + mInstallTasks.size());
	}
	
	
	private void mUpdateNotification(String layerName, InstallTaskState state, int progress)
	{
		int id = mNotificationIdMap.get(layerName);
		int percent = Math.round(progress);
		Intent resultIntent = new Intent(this, LayerListActivity.class);

		String title = getString(R.string.installing_layer) + " " + layerName;
		String text;
		if(state == InstallTaskState.DOWNLOADING)
			text = getString(R.string.downloading_layer) + " " +  layerName + " [" + progress + "%]";
		else if(state == InstallTaskState.INSTALLING)
			text = getString(R.string.installing_layer) +  " " +  layerName + " [" + progress + "%]";
		else if(state == InstallTaskState.INSTALL_ERROR)
		{
			text = getString(R.string.error_installing_layer) +  " " +  layerName;
			progress = 100; /* do not show progress */
		}
		else if(state == InstallTaskState.DOWNLOAD_ERROR)
		{
			progress = 100; /* do not show progress */
			text = getString(R.string.error_downloading_layer) +  " " +  layerName;
		}
		else if(state == InstallTaskState.DOWNLOAD_CANCELLED)
		{
			progress = 100; /* do not show progress */
			text = getResources().getString(R.string.download_cancelled);
		}
		else if(state == InstallTaskState.INSTALL_COMPLETE)
			text = getString(R.string.successfully_installed_layer) +  " " +  layerName;
		else
			text = "Dont know what I'm doin'";
		
		int iconId = android.R.drawable.stat_sys_download;
		if(progress == 100)
			iconId = android.R.drawable.stat_sys_download_done;
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(LayerListActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager mNotifyManager =
		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle(title)
		    .setContentText(text)
		    .setSmallIcon(iconId);
		if(progress < 100)
			mBuilder.setProgress(100, percent, false);
		
		mBuilder.setContentIntent(resultPendingIntent);
		mNotifyManager.notify(id, mBuilder.build());
	}

}
