package it.giacomos.android.wwwsapp.service;

import it.giacomos.android.wwwsapp.network.Urls;
import it.giacomos.android.wwwsapp.preferences.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class UpdateMyLocationService extends Service
        implements FetchRequestsTaskListener, Runnable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private Location mLocation;
    private Handler mHandler;
    private GoogleApiClient mGApiClient;
    private UpdateMyLocationTask mUpdateMyLocationTask;
    private long mSleepInterval;
    private boolean mIsStarted;
    private Settings mSettings;
    private String mGcmToken, mAccount;
    /* timestamp updated when the AsyncTask completes, successfully or not */
    private long mLastTaskStartedTimeMillis;
    private long mCheckIfNeedRunIntervalMillis;

    public UpdateMyLocationService()
    {
        super();
        mHandler = null;
        mGApiClient = null;
        mLocation = null;
        mUpdateMyLocationTask = null;
        mIsStarted = false;
        mCheckIfNeedRunIntervalMillis = 20000;
        mGcmToken = "";
        mAccount = "";
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    /**
     * If wi fi network is enabled, I noticed that turning on 3G network as well
     * produces this method to be invoked another times. That is, the ConnectivityChangedReceiver
     * triggers a Service start command. In this case, we must avoid that the handler schedules
     * another execution of the timer.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //   Logger.log("RDS.onStartCmd: intent " + intent + "isStarted" + mIsStarted);
        if (!mIsStarted)
        {
            mSettings = new Settings(this);
            mGcmToken = mSettings.getGcmToken();
            mSleepInterval = mSettings.getServiceSleepIntervalMillis();
            Log.e("RepoDataSrvc.onStartCmd", "service started sleep interval " + mSleepInterval);
            /* the last time the network was used is saved so that if the service is killed and
			 * then restarted, we avoid too frequent and unnecessary downloads
			 */
            mLastTaskStartedTimeMillis = mSettings.getLastReportDataServiceStartedTimeMillis();
            mCheckIfNeedRunIntervalMillis = mSleepInterval / 6;

            if (mGApiClient == null)
                mGApiClient = new GoogleApiClient.Builder(this).
                        addApi(LocationServices.API)
                        .addApi(Plus.API)
                        .addScope(Plus.SCOPE_PLUS_PROFILE)
                        .addConnectionCallbacks(this).
                                addOnConnectionFailedListener(this).build();

			/* if onStartCommand is called multiple times,we must stop previously
			 * scheduled runs.
			 */
            if (mHandler != null)
                mHandler.removeCallbacks(this);
            mHandler = new Handler();
            mHandler.postDelayed(this, 3000);
            mIsStarted = true;
        }
        else
        {
            Log.e("RepoDataSrvc.onStartCmd", "* service is already running");
        }
        return Service.START_STICKY;
    }

    @Override
    /** This method is executed when mCheckIfNeedRunIntervalMillis time interval has elapsed.
     *  The mCheckIfNeedRunIntervalMillis is an interval shorter than mSleepInterval used to
     *  check whether it is time to update or not. If the device goes to sleep, the timers
     *  are suspended and mSleepInterval may result too long in order to get an update in a
     *  reasonable time. Checking often with a simple comparison should be lightweight and
     *  a good compromise to provide a quick update when the phone wakes up.
     *  It connects the location client in order to wait for an available Location.
     */
    public void run()
    {
        long currentTimeMillis = System.currentTimeMillis();
		/* do we need to actually proceed with update task? */
        if (currentTimeMillis - mLastTaskStartedTimeMillis >= mSleepInterval)
        {
			/* wait for connection and then get location and update data */
            mGApiClient.connect();
        }
        else /* check in a while */
        {
            mHandler.postDelayed(this, mCheckIfNeedRunIntervalMillis);
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     * The return value is the best, most recent location, based
     * on the permissions your app requested and the currently-enabled
     * location sensors.
     * <p>
     * After getting the last location, we can start the data fetch task.
     * If for some reason mLocation is null (should not happen!) or the network
     * is down (yes, we check again!), then no task is executed and a next
     * schedule takes place by means of postDelayed on the handler (see the end
     * of the method).
     */
    @Override
    public void onConnected(Bundle arg0)
    {
        // Log.e("LayerInstallService.onConnected", "getting last location");
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGApiClient);
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGApiClient);
        mAccount = Plus.AccountApi.getAccountName(mGApiClient);
        if (mLocation != null && mAccount != null && mGcmToken != null && !mGcmToken.isEmpty())
        {
            startTask();
			/* mark the last execution complete timestamp */
            mLastTaskStartedTimeMillis = System.currentTimeMillis();
			/* save in case the service is killed and then restarted */
            mSettings.setLastReportDataServiceStartedTimeMillis(mLastTaskStartedTimeMillis);
			/* when the task is started, we start the short time check */
            mHandler.postDelayed(this, mCheckIfNeedRunIntervalMillis);
        }
        else/* wait an entire mSleepInterval before retrying */
            mHandler.postDelayed(this, mSleepInterval);

        mGApiClient.disconnect(); /* immediately */
    }

    private void startTask()
    {
		/* check that the network is still available */
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected())
        {
			/* if a task is still running, cancel it before starting a new one */
            if (mUpdateMyLocationTask != null && mUpdateMyLocationTask.getStatus() != AsyncTask.Status.FINISHED)
                mUpdateMyLocationTask.cancel(false);
			/* get the device id */
            String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

				/* start the service and execute it. When the thread finishes, onServiceDataTaskComplete()
				 * will schedule the next task.
				 */
            mUpdateMyLocationTask = new UpdateMyLocationTask(this,
                    mAccount,
                    deviceId,
                    mLocation.getLatitude(),
                    mLocation.getLongitude());
				/* 
				 * "http://www.giacomos.it/meteo.fvg/update_my_location.php";
				 */
            mUpdateMyLocationTask.execute(new Urls().getUpdateMyLocationUrl());
        }
    }

    @Override
    public void onDestroy()
    {
        //   Logger.log("RDS.onDestroy");
		/* clean tasks, stop scheduled repetition of data task, disconnect from 
		 * location service.
		 */
        if (mUpdateMyLocationTask != null)
        {
            mUpdateMyLocationTask.removeFetchRequestTaskListener();
            mUpdateMyLocationTask.cancel(false);
        }
        if (mHandler != null)
            mHandler.removeCallbacks(this);

        if (mGApiClient != null && mGApiClient.isConnected())
            mGApiClient.disconnect();

        mIsStarted = false;

        super.onDestroy();
        // log("x: service destroyed" );
    }

    @Override
    public void onServiceDataTaskComplete(boolean error, String dataAsString)
    {
        boolean notified = false;
        short requestsCount = 0;
        Log.e("RepDataSrvc.onSrvcDatTskCompl", "UpdateMyLocationService complete: data: " + dataAsString);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
		/* LocationClient.connect() failed. No onConnected callback will be executed,
		 * so no task will be started. Just schedule another try, but directly sleep for
		 * mSleepInterval, do not try to reconnect too fast, so do not postDelayed of 
		 * mCheckIfNeedRunIntervalMillis.
		 */
        // Log.e("LayerInstallService.onConnectionFailed", "connection to location failed sleeping for "  + mSleepInterval);
        mHandler.postDelayed(this, mSleepInterval);
    }

    private void log(String message)
    {
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PrintWriter out;
        try
        {
            out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
            out.append(Calendar.getInstance().getTime().toLocaleString() + ": " + message + "\n");
            out.close();
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        // TODO Auto-generated method stub

    }
}
