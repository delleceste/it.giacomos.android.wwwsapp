package it.giacomos.android.wwwsapp.service.sharedData;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import it.giacomos.android.wwwsapp.Logger;
import it.giacomos.android.wwwsapp.preferences.Settings;

/** This class manages the notifications.
 * Since the service can be killed at any time by Android, this class has the 
 * responsibility to save data each time it is modified. The saved data is 
 * the NotificationData (2 objects at most, one request and one report), and the
 * timestamp in milliseconds of the last notification.
 * 
 * Each time a NotificationData changes or the notification timestamp is renewed,
 * the changes must be saved. SharedPreferences are used by the helper class 
 * SharedDataSaveRestre in order to save and restore the objects used by
 * ServiceSharedData.
 * 
 * When the Instance() method is invoked, data is loaded from SharedPreferences
 * (timestamp and notification data).
 * 
 * In this way, if Android kills the Service the notification ServiceSharedData 
 * should not be lost across service restarts.
 * 
 * @author giacomo
 *
 */
public class ServiceSharedData 
{
	private static ServiceSharedData _instance = null;
	private SharedDataSaveRestore mSharedDataSaveRestore;

	/* maps the notification data type into the corresponding notification data.
	 * For each type, only one notification data can be present.
	 * (No more than one notification at time)
	 */
	private HashMap<Short, NotificationData> mNotificationDataHash;
	
	public static ServiceSharedData Instance(Context ctx)
	{
		if(_instance == null)
			_instance = new ServiceSharedData(ctx);
		return _instance;
	}

	/** We have to restore the previous state in case the service gets destroyed
	 * and recreated
	 * 
	 * @param ctx the application context
	 */
	private ServiceSharedData(Context ctx)
	{
		mSharedDataSaveRestore = 
				new SharedDataSaveRestore(ctx.getSharedPreferences(Settings.PREFERENCES_NAME,
						Context.MODE_PRIVATE));
		mNotificationDataHash = mSharedDataSaveRestore.loadNotificationData();
	}

	public NotificationData getNotificationData(short type)
	{
		return mNotificationDataHash.get(type);
	}

	public NotificationData get(short type)
	{
		if(mNotificationDataHash.containsKey(type))
			return mNotificationDataHash.get(type);
		return null;
	}
	
	public void updateCurrentRequest(NotificationData notificationData, boolean setNotified) 
	{
		short type = notificationData.getType();
		/* replace old notificationData for the specified type.
		 * Remember that only one notificationData per type can be tracked.
		 */
		mNotificationDataHash.put(type, notificationData);
		/* save data on shared preferences */
		mSharedDataSaveRestore.saveNotificationData(mNotificationDataHash);
		
		if(setNotified)
		{
			/* save */
			mSharedDataSaveRestore.setLastNotifiedTimeMillis(notificationData.getTag(), System.currentTimeMillis());
		}
	}
	
	public boolean arrivesTooEarly(NotificationData notificationData, Context ctx)
	{
		/* we can filter out subsequent requests basing on the minimum time between notifications
		 * desired by the user.
		 */
		long minMillis = new Settings(ctx).minTimeBetweenNotificationsMinutes(notificationData.getTag()) * 60 * 1000;
		long diffTimeMs =  Calendar.getInstance().getTime().getTime() - mSharedDataSaveRestore.getLastNotifiedTimeMillis(notificationData.getTag());
		Log.e("ServiceSharedData.canBeConsideredNew", "difftime ms = " + diffTimeMs + " min millis " + minMillis);
		short type = notificationData.getType();
		boolean isDifferentType = !mNotificationDataHash.containsKey(type);
		
		
		/// TEMP!! To test notifications without waiting minTimeBetweenNotificationsMinutes
		/// minMillis = 10000;
		
		if(diffTimeMs < minMillis && !isDifferentType)
		{
//			Logger.log("SSD.canBeConsideredNew: no: diffTime " + diffTimeMs + " < " + minMillis);
			Log.e("ServiceSharedData", "diffTimeMillis < minMillis TOO EARLY!!");
			return true; /* too early */
		}
		
//		Log.e("ServiceSharedData", "diffTimeMillis > minimum --> NEW");
		return false; /* elapsed time is greater than the minimum interval required between notifications */
	}
	
	public boolean alreadyNotifiedEqual(NotificationData notificationData) 
	{
		NotificationData inHashND;
		short type = notificationData.getType();
		if(!mNotificationDataHash.containsKey(type))
		{
			Log.e("canBeConsideredNew", "SSD.canBeConsideredNew: yes: no notif. for type " + type);
			return false;
		}
		else
			inHashND = mNotificationDataHash.get(type);

		/* if the notification is exactly the same, never trigger it again. */
		if(inHashND.equals(notificationData))
		{
			Log.e("canBeConsideredNew", "SSD.canBeConsideredNew: no: identical notifications for type " + type);
			return true; /* exactly the same */
		}
		return false; /* not equal, not already notified */
	}

	public boolean arrivesTooLate(NotificationData notificationData) 
	{
		Date notifDate = notificationData.getDate();
		Log.e("ServiceSharedData.arrivesTooLate", " notif delay ms " + (System.currentTimeMillis() - notifDate.getTime()));
		return (System.currentTimeMillis() - notifDate.getTime()) > 1000 * 60 * 60 * 3;
	}

}
