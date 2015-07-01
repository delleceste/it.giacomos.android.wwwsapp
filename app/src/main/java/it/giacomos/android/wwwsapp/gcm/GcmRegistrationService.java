package it.giacomos.android.wwwsapp.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.HttpPostParametrizer;
import it.giacomos.android.wwwsapp.network.HttpWriteRead;
import it.giacomos.android.wwwsapp.network.Urls;
import it.giacomos.android.wwwsapp.preferences.Settings;

/**
 * Created by giacomo on 30/06/15.
 */
public class GcmRegistrationService extends IntentService
{
    private static final String TAG = "GcmRegistrationService";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public GcmRegistrationService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String token = "";
        boolean tokenChanged = false;
        String account = intent.getStringExtra("account");
        String device_id = intent.getStringExtra("device_id");
        String oldToken = intent.getStringExtra("old_token");

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.e(TAG, "GCM Registration Token: " + token);

                tokenChanged = (oldToken.compareTo(token) != 0);
                tokenChanged = true;
                if(tokenChanged) /* token changed: update it on the server */
                {
                    Log.e("GcmRegServ.onHandleInt", "FORCED!!! FORCED!!! FORCED!!!  token changed from old " + oldToken + " to new " + token);
                    if (!sendRegistrationToServer(token, account, device_id))
                        token = ""; /* failure: invalidate token */
                    /* changed token is saved in HelloWorldActivity after REGISTRATION_COMPLETE intent is received */
                }
                else
                    Log.e("GcmRegServ.onHandleInt", " token unchanged, not sending to server!");
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed, so that the gcmToken is updated in the settings if necessary. */
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        registrationComplete.putExtra("gcmTokenChanged", tokenChanged);
        registrationComplete.putExtra("gcmToken", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private boolean sendRegistrationToServer(String token, String account, String device_id)
    {
        boolean success = false;
        Log.e("GcmRegServ.sendRegToSer", " Sending new token into server");
        HttpWriteRead httpWriteRead = new HttpWriteRead("GcmRegistrationService");
        HttpPostParametrizer p = new HttpPostParametrizer();
        p.add("account", account);
        p.add("token", token);
        p.add("device_id", device_id);
        httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_RESPONSE_VALID_IF_ZERO);
        success = httpWriteRead.read(new Urls().getRegisterGCMTokenUrl(), p.toString());
        if(!success)
            Log.e("GcmRegServ.sendRegToSer", "error " + httpWriteRead.getError());
        return success;
    }
}
