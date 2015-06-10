package it.giacomos.android.wwwsapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.Urls;

/**
 * Created by giacomo on 8/06/15.
 */
public class PostDataService extends IntentService
{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public PostDataService()
    {
        super("PostDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String data = "", response  = "";
        String error = "";
        Urls myUrls = new Urls();
        URL url = null;
        String serviceName = "Generic PostDataService";
        if(intent.hasExtra("params") && intent.hasExtra("url") && intent.hasExtra("serviceName"))
        {
            serviceName = intent.getStringExtra("serviceName");
            try
            {
                url = new URL(intent.getStringExtra("url"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                data = intent.getStringExtra("params");
                Log.e("PostDataS.onHandleInt", "url: " + url.toString() + " - data " + data);
                OutputStreamWriter wr;
                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String currentLine;
                while((currentLine = in.readLine()) != null)
                    response += currentLine + "\n";
                if(response.trim().compareTo("0") != 0) /* trim trailing \n */
                    error = response;
                in.close();
                Log.e("PostDataS.onHandleI", " response \"" + response + "\"");

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
            error = getString(R.string.post_data_service_missing_params);

        Intent reportIntent = new Intent(HelloWorldActivity.REPORT_DATA_SERVICE_INTENT);
        reportIntent.putExtra("serviceName", serviceName);
        if(error.length() > 0)
        {
            Log.e("PostDataS.onHandleI", " Error " + error);
            reportIntent.putExtra("type", "error");
            reportIntent.putExtra("text", error);
        }
        else
        {
            reportIntent.putExtra("type", "success");
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(reportIntent);
    }
}
