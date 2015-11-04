package it.giacomos.android.wwwsapp.personalMessageActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.wwwsapp.network.HttpPostParametrizer;
import it.giacomos.android.wwwsapp.network.HttpWriteRead;

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

		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
		parametrizer.add("d", mDeviceId);
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
			else
				mDataAsText = document; /* either 0 or the xml document */

		}
		//   Log.e("PersonalMessageDataTask.doInBackground", " fetching data from " + urls[0]);
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
