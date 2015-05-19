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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

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
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urls[0]);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cli", CLI));
        postParameters.add(new BasicNameValuePair("last_read_on", String.valueOf(mLastNewsReadTimestamp)));
        Log.e("PersonalMessageDataFetchTask", "timestamp last was " + mLastNewsReadTimestamp);
        UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(postParameters);
	        request.setEntity(form);
	        HttpResponse response = httpClient.execute(request);
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
	        	mErrorMsg = statusLine.getReasonPhrase();
	        else if(statusLine.getStatusCode() < 0)
	        	mErrorMsg = "Server error";
	        /* check the echo result */
	        HttpEntity entity = response.getEntity();
	        String document = EntityUtils.toString(entity);
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
	    							Log.e("PersonalMessageDataFetchTask", "date " + date + ", url " + url + ", text " + text);
	    							if(date != null && url != null && !date.isEmpty() && !url.isEmpty() && !text.isEmpty())
	    							{
	    								mNewsData = new NewsData(date, time, text, url);
	    							}
	    						}
	    					}
	    				} 
	    				catch (SAXException e) 
	    				{
	    					Log.e("PersonalMessageDataFetchTask SAXException: doInBackground()", e.getLocalizedMessage());
	    				} 
	    				catch (IOException e) 
	    				{	
	    					Log.e("PersonalMessageDataFetchTask: doInBackground()", e.getLocalizedMessage());
	    				}
	    			} 
	    			catch (UnsupportedEncodingException e) 
	    			{
	    				Log.e("PersonalMessageDataFetchTask: doInBackground()", e.getLocalizedMessage());
	    			}
	    		} 
	    		catch (ParserConfigurationException e1) 
	    		{
	    			Log.e("PersonalMessageDataFetchTask: doInBackground()", e1.getLocalizedMessage());
	    		}		
	        }
		}
		catch(IllegalArgumentException e) /* ANR fix: hostname may not be null */
		{
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) 
		{
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} catch (IOException e) {
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
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
		Log.e("PersonalMessageDataFetchTask", "task cancelled");
		if(doc != null)
			doc = null;
	}

}
