package it.giacomos.android.wwwsapp.service.sharedData;

import android.os.Bundle;

import java.util.ArrayList;

public class NotificationDataFactory 
{
	public boolean isNewRadarImageNotification(String input)
	{
		return (input != null && input.startsWith("I:"));
	}
	
	public NotificationData parse(Bundle input)
	{
		NotificationData nData = null;
		if(input.getString("type").compareTo("request") == 0)
			nData = new ReportRequestNotification(input);
		else if(input.getString("type").compareTo("report") == 0)
			nData = new ReportNotification("");
		return nData;
	}
}
