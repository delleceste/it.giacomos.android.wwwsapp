package it.giacomos.android.wwwsapp.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by giacomo on 30/06/15.
 */
public class HelloWorldInstanceIDListenerService extends InstanceIDListenerService
{
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        Log.e("HelloWInstanceIDListSrv", "onTokenRefresh. Starting service GcmRegistrationService");
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, GcmRegistrationService.class);
        startService(intent);
    }
    // [END refresh_token]
}
