package it.giacomos.android.wwwsapp.gcm;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.wwwsapp.MyAlertDialogFragment;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.preferences.Settings;

public class GcmRegistrationManager 
{
	/* project number obtained from
	 * https://console.developers.google.com/project/871762795415
	 */
	private String SENDER_ID = "871762795415";
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context)
	{
		Settings s = new Settings(context);
	    String registrationId = s.getGcmRegistrationId();
	    if (registrationId.isEmpty()) {
	        Log.e("GcmRegistrationManager.getRegistrationId", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    
	    if (versionChanged(context)) 
	    	registrationId = "";
	    
	    return registrationId;
	}
	
	public boolean versionChanged(Context context)
	{
		Settings s = new Settings(context);
		int registeredVersion = s.getLastGCMRegisteredAppVersionId();
	    int currentVersion = 0;
		try {
			currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	    if (registeredVersion != currentVersion) {
	    	Log.e("GcmRegistrationManager.versionChanged",  "App version changed from " + registeredVersion + " to "
	    			+ currentVersion);
	        return true;
	    }
	    return false;
	}
	
	public void saveRegistrationId(Context context, String regId)
	{
		Settings s = new Settings(context);
	    s.saveRegistrationId(regId, context);
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	public void registerInBackground(Context ctx) 
	{
		final Context context = ctx;
		
	    new AsyncTask<Void, Void, String>() 
	    {
	        @Override
	        protected String doInBackground(Void... params) {
	            String regId = "";
	            try {
	                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
	                regId = gcm.register(SENDER_ID);

	            } catch (IOException ex) {
	                regId = "";
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	                /* NOTE the registration is attempted inside init(), called upon onCreate in
	                 * HelloWorldActivity.
	                 */
	            }
	            return regId;
	        }

	        @Override
	        protected void onPostExecute(String regId) 
	        {
	        	Settings s = new Settings(context);
	    	    s.saveRegistrationId(regId, context);
	    	    if(regId.isEmpty())
	    	    	Toast.makeText(context, R.string.gcm_registered_failed, Toast.LENGTH_LONG ).show();
	    	    Toast.makeText(context, context.getString(R.string.gcm_registered_ok), Toast.LENGTH_LONG).show();
	        }

			
	    }.execute(null, null, null);
	    
	}
}
