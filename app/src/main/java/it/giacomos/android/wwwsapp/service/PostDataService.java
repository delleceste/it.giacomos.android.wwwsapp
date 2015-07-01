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
import it.giacomos.android.wwwsapp.network.HttpWriteRead;
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
            boolean ok;
            serviceName = intent.getStringExtra("serviceName");
            HttpWriteRead httpWriteRead = new HttpWriteRead(serviceName);
            /* require that response is "0" for this request */
            httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_RESPONSE_VALID_IF_ZERO);
            ok = httpWriteRead.read(intent.getStringExtra("url"), intent.getStringExtra("params"));
            if(ok) /* response is simply "0" */
                httpWriteRead.getResponse();
            else
                error = httpWriteRead.getError();
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
