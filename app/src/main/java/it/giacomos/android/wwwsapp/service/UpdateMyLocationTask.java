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

import it.giacomos.android.wwwsapp.network.HttpPostParametrizer;
import it.giacomos.android.wwwsapp.network.HttpWriteRead;
import it.giacomos.android.wwwsapp.network.Urls;

public class UpdateMyLocationTask extends AsyncTask<String, Integer, String> {

	private String mErrorMsg;
	String mDeviceId, mAccount;
	private FetchRequestsTaskListener mServiceDataTaskListener;
	double mLatitude, mLongitude;

	private static String CLI = "afe0983der38819073rxc1900lksjd";

	public UpdateMyLocationTask(FetchRequestsTaskListener sdtl,
								String account,
			String deviceId, 
			double lat,
			double longit)
	{
		mErrorMsg = "";
		mServiceDataTaskListener = sdtl;
		mDeviceId = deviceId;
		mLatitude = lat;
		mLongitude = longit;
		mAccount = account;
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
		String url = new Urls().getUpdateMyLocationUrl();
		String serviceName = "RegisterUserService";
		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
		parametrizer.add("account", mAccount);
		parametrizer.add("d", mDeviceId);
		parametrizer.add("la", mLatitude);
		parametrizer.add("lo", mLongitude);
		String params = parametrizer.toString();

		HttpWriteRead httpWriteRead = new HttpWriteRead("UpdateMyLocationTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_RESPONSE_VALID_IF_ZERO);
		if(!httpWriteRead.read(url, params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
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
