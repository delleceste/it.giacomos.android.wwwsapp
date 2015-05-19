package it.giacomos.android.wwwsapp.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

import android.os.AsyncTask;
import android.util.Log;

public class UpdateMyLocationTask extends AsyncTask<String, Integer, String> {

	private String mErrorMsg;
	String mDeviceId, mRegistrationId;
	private FetchRequestsTaskListener mServiceDataTaskListener;
	double mLatitude, mLongitude;
	GPlusUserInfo mGPlusUInfo;

	private static String CLI = "afe0983der38819073rxc1900lksjd";

	public UpdateMyLocationTask(FetchRequestsTaskListener sdtl, 
			String deviceId, 
			String registrationId, 
			GPlusUserInfo gpui,
			double lat, 
			double longit)
	{
		mErrorMsg = "";
		mServiceDataTaskListener = sdtl;
		mDeviceId = deviceId;
		mRegistrationId = registrationId;
		mLatitude = lat;
		mLongitude = longit;
		mGPlusUInfo = gpui;
	}

	public void removeFetchRequestTaskListener()
	{
		mServiceDataTaskListener = null;
	}

	@Override
	protected String doInBackground(String... urls) 
	{
		String data = "";
		mErrorMsg = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(urls[0]);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("cli", CLI));
		postParameters.add(new BasicNameValuePair("d", mDeviceId));
		postParameters.add(new BasicNameValuePair("rid", mRegistrationId));
		postParameters.add(new BasicNameValuePair("la", String.valueOf(mLatitude)));
		postParameters.add(new BasicNameValuePair("lo", String.valueOf(mLongitude)));
		postParameters.add(new BasicNameValuePair("account", mGPlusUInfo.account));
		postParameters.add(new BasicNameValuePair("display_name", mGPlusUInfo.displayName));
		postParameters.add(new BasicNameValuePair("gplus_url", mGPlusUInfo.url));
		
	//	Log.e("UpdateMyLocationTask", "rid " + mRegistrationId + ", d " + mDeviceId);
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
			else /* ok */
			{
				HttpEntity entity = response.getEntity();
				data = EntityUtils.toString(entity);
				if(data.compareTo("0") != 0)
					mErrorMsg = data;
			}
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
		catch (SecurityException se)
		{
			/* sometimes signaled as ANR.. strange. */
			mErrorMsg = se.getLocalizedMessage();
			se.printStackTrace();
		}
		return data;
	}

	@Override
	public void onPostExecute(String data)
	{
		if(mServiceDataTaskListener != null)
		{
			if(mErrorMsg.isEmpty())
				mServiceDataTaskListener.onServiceDataTaskComplete(false, data);
			else
				mServiceDataTaskListener.onServiceDataTaskComplete(true, mErrorMsg);
		}
	}
	
	@Override
	public void onCancelled(String data)
	{
//		Log.e("UpdateMyLocationTask.onCancelled", "task cancelled");
	}
}
