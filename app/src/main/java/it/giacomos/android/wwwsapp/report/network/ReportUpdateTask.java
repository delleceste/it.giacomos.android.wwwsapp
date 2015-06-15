package it.giacomos.android.wwwsapp.report.network;

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
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

public class ReportUpdateTask extends AsyncTask<String, Integer, String> 
{
	private static final String CLI = "afe0983der38819073rxc1900lksjd";
	private String mErrorMsg;
	private ReportUpdateTaskListener mReportUpdateTaskListener;
	LatLngBounds mArea;

	public ReportUpdateTask(ReportUpdateTaskListener reportUpdateTaskListener, LatLngBounds area)
	{
		mReportUpdateTaskListener = reportUpdateTaskListener;
		mArea = area;
	}

	@Override
	public void onPostExecute(String doc)
	{
        Log.e("RepUpdTask.onPostExec", " got " + doc);
		mReportUpdateTaskListener.onReportUpdateTaskComplete(!mErrorMsg.isEmpty(), doc);
		mArea  = null;
	}
	
	@Override
	public void onCancelled(String doc)
	{
        Log.e("ReportUpdateTask.onCanc", "cancelled task");
		mArea  = null;
	}
	
	public String getError()
	{
		return mErrorMsg;
	}

	public LatLngBounds getArea()
	{
		return mArea;
	}

	public boolean isProcessingArea(LatLngBounds area)
	{
		return mArea != null && mArea == area;
	}

	@Override
	protected String doInBackground(String... urls) 
	{
		String document = "";
		synchronized (mArea)
		{
			mErrorMsg = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(urls[0]);
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("cli", CLI));
			UrlEncodedFormEntity form;
			try
			{
				form = new UrlEncodedFormEntity(postParameters);
				request.setEntity(form);
				HttpResponse response = httpClient.execute(request);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
					mErrorMsg = statusLine.getReasonPhrase();
				else if (statusLine.getStatusCode() < 0)
					mErrorMsg = "Server error";
	        /* check the echo result */
				HttpEntity entity = response.getEntity();
				document = EntityUtils.toString(entity);
			} catch (UnsupportedEncodingException e)
			{
				mErrorMsg = e.getLocalizedMessage();
				e.printStackTrace();
			} catch (ClientProtocolException e)
			{
				mErrorMsg = e.getLocalizedMessage();
				e.printStackTrace();
			} catch (IOException e)
			{
				mErrorMsg = e.getLocalizedMessage();
				e.printStackTrace();
			}
		    mArea  = null;
		}
		return document;
	}

	
}
