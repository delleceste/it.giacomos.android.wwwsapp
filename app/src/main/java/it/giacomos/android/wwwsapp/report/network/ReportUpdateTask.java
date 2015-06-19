package it.giacomos.android.wwwsapp.report.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.HttpPostParametrizer;

public class ReportUpdateTask extends AsyncTask<String, Integer, String[]>
{
    private static final String CLI = "afe0983der38819073rxc1900lksjd";
    private String mErrorMsg;
    private ReportUpdateTaskListener mReportUpdateTaskListener;
    LatLngBounds mArea;

    public ReportUpdateTask(ReportUpdateTaskListener reportUpdateTaskListener, LatLngBounds area)
    {
        mReportUpdateTaskListener = reportUpdateTaskListener;
        mArea = area;
        mErrorMsg = "";
    }

    @Override
    public void onPostExecute(String[] data)
    {
        Log.e("RepUpdTask.onPostExec", " got " + data[1] + " for " + data[0]);
        mReportUpdateTaskListener.onReportUpdateTaskComplete(!mErrorMsg.isEmpty(), data);
        mArea = null;
    }

    @Override
    public void onCancelled(String[] doc)
    {
        Log.e("ReportUpdateTask.onCanc", "cancelled task");
        mArea = null;
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
    /**
     * @param  task_data: task_data[0] = REPORT_URL;
     * task_data[1] = mLayerName;
     */
    protected String[] doInBackground(String... task_data)
    {
        Log.e("RepUpdTask.doInBg", "1. layer " + task_data[1] + " URL  " + task_data[0]);
        String[] document = new String[2];
        synchronized (mArea)
        {
            URL url = null;
            String layerName = task_data[1];
            String accountName = task_data[2];
            document[0] = task_data[1]; /* this will be returned */
            try
            {
                document[1] = "";
                url = new URL(task_data[0]);
                HttpPostParametrizer httpPostParametrizer = new HttpPostParametrizer();
                httpPostParametrizer.add("layer", layerName);
                httpPostParametrizer.add("account", accountName);
                httpPostParametrizer.add("sw_lat", mArea.southwest.latitude);
                httpPostParametrizer.add("sw_lon", mArea.southwest.longitude);
                httpPostParametrizer.add("ne_lat", mArea.northeast.latitude);
                httpPostParametrizer.add("ne_lon", mArea.northeast.longitude);
                String data = httpPostParametrizer.toString();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr;
                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                wr.close();

                Log.e("RepUpdTask.doInBg", "2. waiting for result for " +url + "?" + data);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String currentLine;
                while ((currentLine = in.readLine()) != null)
                {
                    document[1] += currentLine + "\n";
                }
                in.close();

            } catch (MalformedURLException e)
            {
                mErrorMsg = e.getLocalizedMessage();
            } catch (UnsupportedEncodingException e)
            {
                mErrorMsg = e.getLocalizedMessage();
            } catch (IOException e)
            {
                mErrorMsg = e.getLocalizedMessage();
            }
        }
        mArea = null;

    return document;
}


}
