package it.giacomos.android.wwwsapp.service.sharedData;

import java.util.ArrayList;

public class NotificationDataFactory 
{
	public boolean isNewRadarImageNotification(String input)
	{
		return (input != null && input.startsWith("I:"));
	}
	
	public NotificationData parse(String input)
	{
		/* return an allocated array even if no data is valuable */
		NotificationData nData = new ReportRequestNotification("TEST TEST TEST");
		return nData;
	}
}
