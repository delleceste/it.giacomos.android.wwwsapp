package it.giacomos.android.wwwsapp.news;

public class NewsData 
{	
	public NewsData(String date, String time, String text, String url) 
	{
		mUrl = url;
		mDate = date;
		mTime = time;
		mText = text;
	}

	public String getText()
	{
		return mText;
	}
	
	public String getDate()
	{
		return mDate;
	}
	
	public String getTime()
	{
		return mTime;
	}
	
	public boolean hasTime()
	{
		return !mTime.isEmpty();
	}
	
	public String getUrl()
	{
		return mUrl;
	}
	
	private String mUrl, mDate, mTime, mText;
}
