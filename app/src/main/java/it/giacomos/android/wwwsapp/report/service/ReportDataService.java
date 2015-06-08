package it.giacomos.android.wwwsapp.report.service;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import it.giacomos.android.wwwsapp.ErrorActivity;
import it.giacomos.android.wwwsapp.MyAlertDialogFragment;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.gcm.GcmRegistrationManager;
import it.giacomos.android.wwwsapp.network.state.Urls;

/**
 * Created by giacomo on 8/06/15.
 */
public class ReportDataService extends IntentService
{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ReportDataService()
    {
        super("ReportDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String data = "", response  = "";
        String error = "";
        Urls myUrls = new Urls();
        URL url = null;
        if(intent.hasExtra("params"))
        {
            try
            {
                url = new URL(myUrls.reportServiceUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                data = intent.getStringExtra("params");
                Log.e("ReportDataS.onHandleInt", "url: " + url.toString() + " - data " + data);
                OutputStreamWriter wr;
                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String currentLine;
                while((currentLine = in.readLine()) != null)
                    response += currentLine + "\n";
                if(response.compareTo("0") != 0)
                    error = response;
                in.close();
                Log.e("ReportDataSrv.onHandleInt", " response " + response);

            } catch (MalformedURLException e)
            {
                error = e.getLocalizedMessage();
            } catch (UnsupportedEncodingException e)
            {
                error = e.getLocalizedMessage();
            } catch (IOException e)
            {
                error = e.getLocalizedMessage();
            }
        }
        else
            error = getString(R.string.report_lat_lon_unavailable);
        if(error.length() > 0)
        {
            Log.e("ReportDataService.onHandleInt", " Error " + error);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//            Intent i = new Intent(this, ErrorActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.putExtra("title", getString(R.string.report_service_error));
//            i.putExtra("text", error);
//            startActivity(i);
        }
        else
            Toast.makeText(this, R.string.report_post_successful, Toast.LENGTH_SHORT).show();
    }
}
