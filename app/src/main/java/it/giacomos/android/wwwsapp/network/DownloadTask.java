package it.giacomos.android.wwwsapp.network;


import java.lang.String;

public class DownloadTask 
{
	public DownloadTask(DownloadTaskType t, String urlStr, int id, int priority)
	{
		mType = t;
		mUrlStr = urlStr;
		mId = id;
		mPriority = priority;
	}
	
	DownloadTaskType getType() { return mType; }
	String getUrl() { return mUrlStr; }
	int getId() { return mId; }
	int getPriority() { return mPriority; }

	DownloadTaskType mType;
	String mUrlStr;
	int mId, mPriority;
}
