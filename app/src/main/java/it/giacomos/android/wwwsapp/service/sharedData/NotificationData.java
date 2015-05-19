package it.giacomos.android.wwwsapp.service.sharedData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public abstract class NotificationData 
{
	public static short TYPE_REQUEST = 0;
	public static short TYPE_REPORT = 1;
	public static short TYPE_RAIN = 2;
	public static short TYPE_NEW_RADAR_IMG = 3;
	public String datetime, username;
	public double latitude, longitude;
	
	protected Date date;
	
	/* if true, the request has been satisfied, if false, it is already 
	 * pending.
	 * When the get_requests.php returns an empty document, it means that
	 * any request concerning our location has been consumed.
	 */
	protected boolean mIsConsumed;
	
	public boolean isRequest()
	{
		return getType() == NotificationData.TYPE_REQUEST;
	}
	
	public boolean isRainAlert()
	{
		return getType() == NotificationData.TYPE_RAIN;
	}
	
	public abstract String getTag();
	
	public abstract int getId();
	
	public abstract short getType();
	
	public abstract boolean isValid();
	
	public Date getDate()
	{
		return date;
	}
	
	public NotificationData()
	{
		latitude = longitude = -1;
		mIsConsumed = false;
		datetime = username = "";
	}
	
	public void setConsumed(boolean consumed)
	{
		mIsConsumed = consumed;
	}
	
	public abstract String toString();
	
	
	/**
	 * @return true if this NotificationData has been consumed (i.e. a request
	 * satisfied or a report visited).
	 * 
	 * This is used by the map view in order to show or not a marker in correspondence
	 * of the location where this data is bound. 
	 * Actually, the notification data is not removed from the service shared data 
	 * until a new notification arrives. In other words, if a notification data for this 
	 * location has been withdrawn, isConsumed will be true but the notification data still
	 * remains.
	 */
	public boolean isConsumed()
	{
		return mIsConsumed;
	}
	
	
	public boolean makeDate(String datestr)
	{
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			date = formatter.parse(datestr);
			return true;
		} 
		catch(Exception e)
		{
			Log.e("NotificationData.makeDate", e.getLocalizedMessage());
			return false;
		}
	}
	
	public boolean equals(NotificationData other)
	{
		Log.e("NotificationData.equals ", "result: " + String.valueOf(other.getType() == getType()) + ", " + 
				String.valueOf( other.latitude == latitude) + ", " + 
				String.valueOf(other.longitude == longitude)  +", " +  String.valueOf(other.date.equals(date)));
		//Log.e("NotificationData.equals ", "date 1 " + date.toLocaleString() + ", other date " + other.date.toLocaleString());
		return other.getType() == getType() && other.latitude == latitude &&
				other.longitude == longitude && other.date != null && date != null && other.date.equals(date);
	}
}
