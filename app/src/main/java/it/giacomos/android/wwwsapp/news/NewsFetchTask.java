package it.giacomos.android.wwwsapp.news;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.wwwsapp.network.HttpPostParametrizer;
import it.giacomos.android.wwwsapp.network.HttpWriteRead;

public class NewsFetchTask extends AsyncTask<String, Integer, String> 
{
	private static String CLI = "afe0983der38819073rxc1900lksjd";
	private long mLastNewsReadTimestamp;
	private String mErrorMsg;
	private NewsData mNewsData;
	private NewsUpdateListener mNewsUpdateListener;
	
	public NewsFetchTask(long lastNewsReadTimestamp, NewsUpdateListener nud)
	{
		mLastNewsReadTimestamp = lastNewsReadTimestamp;
		mNewsUpdateListener = nud;
	}
	
	@Override
	protected String doInBackground(String... urls)
	{
		mNewsData = null; /* to use if nothing to do or on error */
		mErrorMsg = "";

		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
		parametrizer.add("cli", CLI);
		parametrizer.add("last_read_on", String.valueOf(mLastNewsReadTimestamp));
		/*  test */
		// postParameters.add(new BasicNameValuePair("before_datetime", "2014-08-23 21:11:00"));
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("UpdateMyLocationTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_ANY_RESPONSE_VALID);
		if(!httpWriteRead.read(urls[0], params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
		}
		else
		{
			String document = httpWriteRead.getResponse();
			if(document.compareTo("-1") == 0)
				mErrorMsg = "Server error: the server returned " + document;
			else if(document.compareTo("0") == 0)
			{
				/* nothing to do */
			}
			else
			{
				/* parse xml and get parameters for news data */
				Document dom;
				DocumentBuilderFactory factory;
				DocumentBuilder builder;
				InputStream is;
				factory = DocumentBuilderFactory.newInstance();
				try {
					builder = factory.newDocumentBuilder();
					try
					{
						is = new ByteArrayInputStream(document.getBytes("UTF-8"));
						try
						{
							dom = builder.parse(is);
							NodeList newsNodes = dom.getElementsByTagName("news");
							NodeList urlNodes = dom.getElementsByTagName("a");
							if(newsNodes.getLength() == 1 && urlNodes.getLength() ==1)
							{
								Element news = (Element) newsNodes.item(0);
								Element a = (Element) urlNodes.item(0);
								if(news != null && a != null)
								{
									String date = news.getAttribute("date");
									String time = news.getAttribute("time"); /* not compulsory */
									String url = a.getAttribute("href");
									Node aNode = a.getFirstChild();
									String text = "";
									if(aNode instanceof CharacterData)
										text = ((CharacterData) aNode).getData();
									Log.e("PerMessDataFetchTsk", "date " + date + ", url " + url + ", text " + text);
									if(date != null && url != null && !date.isEmpty() && !url.isEmpty() && !text.isEmpty())
									{
										mNewsData = new NewsData(date, time, text, url);
									}
								}
							}
						}
						catch (SAXException e)
						{
							Log.e("PerMDataFetTsk: doInBg", e.getLocalizedMessage());
						}
						catch (IOException e)
						{
							Log.e("PerMDataFetTsk: doInBg", e.getLocalizedMessage());
						}
					}
					catch (UnsupportedEncodingException e)
					{
						Log.e("PerMDataFetTsk: doInBg", e.getLocalizedMessage());
					}
				}
				catch (ParserConfigurationException e1)
				{
					Log.e("PerMDataFetTsk: doInBg", e1.getLocalizedMessage());
				}
			}
		}
		return null;
	}
	
	public void onPostExecute(String doc)
	{
		if(mNewsData != null)
			mNewsUpdateListener.onNewsUpdateAvailable(mNewsData);
	}

	public void onCancelled(String doc)
	{
		Log.e("PerMessDataFetchTsk", "task cancelled");
		if(doc != null)
			doc = null;
	}

}
