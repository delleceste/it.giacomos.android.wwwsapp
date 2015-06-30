package it.giacomos.android.wwwsapp.gcm;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.preferences.Settings;
import it.giacomos.android.wwwsapp.service.sharedData.NotificationData;
import it.giacomos.android.wwwsapp.service.sharedData.NotificationDataFactory;
import it.giacomos.android.wwwsapp.service.sharedData.RainNotification;
import it.giacomos.android.wwwsapp.service.sharedData.ReportRequestNotification;
import it.giacomos.android.wwwsapp.service.sharedData.ServiceSharedData;

import java.util.ArrayList;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService
{
    private static final String TAG = "GcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        String message = data.getString("message");
        Log.e(TAG, "From: " + from);
        Log.e(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        boolean notified = false;
        String dataAsString = message;
        long timestampSeconds = 0;
        long currentTimestampSecs = System.currentTimeMillis() / 1000;
        try
        {
            timestampSeconds = Long.parseLong(data.getString("timestamp"));
        } catch (NumberFormatException e)
        {

        }
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationDataFactory notificationDataFactory = new NotificationDataFactory();
        NotificationData notificationData = notificationDataFactory.parse(dataAsString);
        int iconId = -1, ledColor = 0;
        if (notificationData.isValid())
        {
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = null;
            resultIntent = new Intent(this, HelloWorldActivity.class);
            resultIntent.putExtra("NotificationReportRequest", true);

						/* if we entered one of the cases above, resultIntent will be not null
                         * We can build and post the notification.
						 */
            if (resultIntent != null)
            {
							/* latitude and longitude needed in every kind of notification */
                resultIntent.putExtra("ptLatitude", notificationData.latitude);
                resultIntent.putExtra("ptLongitude", notificationData.longitude);

                int notificationFlags = Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS;
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(iconId)
                                .setAutoCancel(true)
                                .setTicker(message)
                                .setLights(ledColor, 1000, 1000)
                                .setContentTitle(getResources().getString(R.string.app_name))
                                .setContentText(message).setDefaults(notificationFlags);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(HelloWorldActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                // mId allows you to update the notification later on.

                int notifId = notificationData.getId();
                String notifTag = notificationData.getTag();
                Notification notification = notificationBuilder.build();
							/* remove previous similar notifications if present */
                mNotificationManager.notify(notifTag, notifId, notification);
                notified = true;
							/* update notification data */
                Log.e("GcmListeService.onRec", "notification setting notified " + notificationData.getTag() + ", " + notified);
            }
        } /* for(NotificationData notificationData : notifications) */
    }
    // [END receive_message]

}
