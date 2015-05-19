package it.giacomos.android.wwwsapp.service.sharedData;

import java.util.Date;

import android.util.Log;

public class RainNotification extends NotificationData {

	private boolean mIsValid;
	private boolean mGoingToRain;
	private float mLastDbZ;

	public String getTag()
	{
		return "RainNotificationTag";
	}
	
	public int getId()
	{
		return 1000;
	}
	
	public RainNotification(String text)
	{
		mGoingToRain = false;
		mLastDbZ = 0.0f;
		mIsValid = false;
		
		String [] parts = text.split("::");
		
		if(parts.length > 5 && parts[0].compareTo("R") == 0)
		{
			datetime = parts[1];
			mIsValid = makeDate(datetime);
			Log.e("RainNotification", "is valid " + mIsValid + " om " + text);
			try
			{
				latitude = Double.parseDouble(parts[2]);
				longitude = Double.parseDouble(parts[3]);
				if(Integer.parseInt(parts[4]) > 0)
					mGoingToRain = true;
			}
			catch (NumberFormatException nfe)
			{
				/* error in number */
			}
			try
			{
				mLastDbZ = Float.parseFloat(parts[5]);
			}
			catch (NumberFormatException nfe)
			{
				/* error in number */
			}
		}
	}
	
	public RainNotification(boolean goingToRain, 
			long timestampSec, 
			float dbZ,
			double lat,
			double lon) 
	{
		mGoingToRain = goingToRain;
		date = new Date(timestampSec * 1000); /* date wants ms */
		mIsValid = (timestampSec > 0);
		mLastDbZ = dbZ;
		latitude = lat;
		longitude = lon;
	}

	public float getLastDbZ()
	{
		return mLastDbZ;
	}
	
	public boolean IsGoingToRain()
	{
		return mGoingToRain;
	}
	
	@Override
	public short getType() {
		return TYPE_RAIN;
	}

	@Override
	public boolean isValid() {
		return mIsValid;
	}

	@Override
	public String toString() 
	{
		int rain;
		if(mGoingToRain)
			rain = 1;
		else
			rain = 0;
		
		return "R::" + datetime + "::" + latitude + "::" + longitude + "::" + rain + "::" + mLastDbZ;
	}
	
	public boolean equals(NotificationData other)
	{
		try{
			RainNotification rN = (RainNotification) other;
			return super.equals(rN) && (mGoingToRain == rN.IsGoingToRain()) && mLastDbZ == rN.getLastDbZ();
		}
		catch(ClassCastException e)
		{
			return false;
		}
	}

}
