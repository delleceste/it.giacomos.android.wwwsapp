package it.giacomos.android.wwwsapp.service.sharedData;

import android.os.Bundle;
import android.util.Log;

public class ReportRequestNotification extends NotificationData
{
	public static int REQUEST_NOTIFICATION_ID = 10988;
	
    public String locality, comment;
	public boolean isRequest;
	private boolean mIsValid;
	private boolean mIsReadOnly;


	public String getTag()
	{
		return "ReportRequestNotification";
	}
	
	public int getId()
	{
		return 1002;
	}
	
	public boolean isValid()
	{
		return mIsValid && latitude > 0 && longitude > 0 && getDate() != null;
	}
	
	public ReportRequestNotification(String input)
	{
		super();

		String parts[] = input.split("::", -1);
		mIsValid = (parts.length == 7 || parts.length == 8);
		if(mIsValid)
		{
			isRequest = (parts[0].compareTo("Q") == 0);
			mIsReadOnly = (parts[1].compareTo("r") == 0);
			datetime = parts[2];
			username = parts[3];
			try
			{
				latitude = Double.parseDouble(parts[4]);
				longitude = Double.parseDouble(parts[5]);
			}
			catch(NumberFormatException e)
			{
				
			}
			locality = parts[6];
			makeDate(datetime);
			if(parts.length > 7)
				mIsConsumed = (parts[7].compareTo("consumed") == 0);
			/* otherwise mIsConsumed remains false */
		}
	}
public ReportRequestNotification(Bundle input) {
	super();
	mIsValid = input.containsKey("sender") && input.containsKey("latitude") && input.containsKey("longitude")
			&& input.containsKey("layer") && input.containsKey("read_only");

	if (mIsValid) {
		isRequest = true;
		mIsReadOnly = (input.getString("read_only").compareTo("true") == 0);
		datetime = input.getString("datetime");
		username = input.getString("sender");
		try {
			latitude = Double.parseDouble(input.getString("latitude"));
			longitude = Double.parseDouble(input.getString("longitude"));
		} catch (NumberFormatException e) {

		}
		makeDate(datetime);
		Log.e("ReportRequestNotification", " made data");
//			if(parts.length > 7)
//				mIsConsumed = (parts[7].compareTo("consumed") == 0);
			/* otherwise mIsConsumed remains false */
		if(input.containsKey("comment"))
			comment = input.getString("comment");
	}

}

	public ReportRequestNotification(String datet, String user, double lat, double lon, String loc)
	{
		super();
		datetime = datet;
		username = user;
		latitude = lat;
		longitude = lon;
		locality = loc;
		mIsValid = true; /* for is valid */
		isRequest = true;
		mIsReadOnly = true;
		makeDate(datetime);
	}

	@Override
	public short getType() {
		
		return NotificationData.TYPE_REQUEST;
	}

	@Override
	public String toString() 
	{
		String ret = "Q::";
		if(mIsReadOnly)
			ret += "r::";
		else
			ret += "w::";
		ret += datetime + "::" + username + "::" + String.valueOf(latitude) + "::";
		ret += String.valueOf(longitude) + "::" + locality;
		
		if(mIsConsumed)
			ret += "::consumed";
		return ret;
	}
}
