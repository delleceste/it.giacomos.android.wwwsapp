package it.giacomos.android.wwwsapp.personalMessageActivity;

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

public class PersonalMessageDataFetchTask extends AsyncTask<String, Integer, String> 
{
	private String mDataAsText;
	private PersonalMessageUpdateListener mPersonalMessageUpdateListener;
	private String mDeviceId;
	private String mErrorMsg;
	
	public PersonalMessageDataFetchTask(String deviceId, PersonalMessageUpdateListener nud)
	{
		mPersonalMessageUpdateListener = nud;
		mDeviceId = deviceId;
		mDataAsText = "";
	}
	
	@Override
	protected String doInBackground(String... urls) 
	{
		mDataAsText = "";
		mErrorMsg = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urls[0]);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("d", mDeviceId));
        UrlEncodedFormEntity form;
        Log.e("PersonalMessageDataTask.doInBackground", " fetching data from " + urls[0]);
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
	        else
	        	mDataAsText = document; /* either 0 or the xml document */
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
		return mDataAsText;
	}
	
	public void onPostExecute(String doc)
	{
		boolean fromCache = false;
		mPersonalMessageUpdateListener.onPersonalMessageUpdate(mDataAsText, fromCache);
	}

	public void onCancelled(String doc)
	{
		Log.e("PersonalMessageDataFetchTask", "task cancelled");
		if(doc != null)
			doc = null;
	}

}
