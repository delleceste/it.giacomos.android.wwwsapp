package it.giacomos.android.wwwsapp.service.sharedData;

import java.util.ArrayList;

public class NotificationDataFactory 
{
	public boolean isNewRadarImageNotification(String input)
	{
		return (input != null && input.startsWith("I:"));
	}
	
	public ArrayList<NotificationData> parse(String input)
	{
		/* return an allocated array even if no data is valuable */
		ArrayList<NotificationData> nDataArray = new ArrayList<NotificationData>();
		if(input != null)
		{
			for(String line : input.split("\n"))
			{
				String parts[] = line.split("::", -1);
				if(parts.length == 7)
					nDataArray.add(new ReportRequestNotification(line));
				else if(parts.length == 5)
					nDataArray.add(new ReportNotification(line));
				else if(parts.length == 6) /* R::date-time::1|0::dbz */
					nDataArray.add(new RainNotification(line));
				else
					/* nothing to do here */
					;
			}
		}
		return nDataArray;
	}
}
