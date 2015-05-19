package it.giacomos.android.wwwsapp.service.sharedData;

public class ReportNotification extends NotificationData
{
	public static int REPORT_NOTIFICATION_ID = 10987;
	
	private boolean mIsValid;
	
	public String getTag()
	{
		return "ReportReceivedNotification";
	}
	
	public int getId()
	{
		return 1001;
	}
	
	public ReportNotification(String input)
	{
		super();
		String parts[] = input.split("::");
		latitude = -1;
		longitude = -1;
		if(parts.length > 4 && parts[0].compareTo("N") == 0)
		{
			try
			{
				latitude = Double.parseDouble(parts[3]);
				longitude = Double.parseDouble(parts[4]);
			}
			catch(NumberFormatException nfe)
			{
				
			}
			username = parts[2];
			datetime = parts[1];
			mIsValid = makeDate(datetime) && latitude > 0 && longitude > 0;
			
		}
		if(parts.length > 5)
			mIsConsumed = (parts[5].compareTo("consumed") == 0);
		
		if(parts.length < 5)
			mIsValid = false;
	}

	@Override
	public short getType() {
		return NotificationData.TYPE_REPORT;
	}
	
	public boolean isValid()
	{
		return mIsValid;
	}

	@Override
	public String toString() 
	{
		/* N::datetime::username::latitude::longitude */
		
		String ret = "N::";
		ret += datetime + "::" + username + "::" + String.valueOf(latitude) + "::";
		ret += String.valueOf(longitude);
		if(mIsConsumed)
			ret += "::consumed";
		
		return ret;
	}
}
