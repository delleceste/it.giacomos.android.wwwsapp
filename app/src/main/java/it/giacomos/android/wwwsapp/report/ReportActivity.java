package it.giacomos.android.wwwsapp.report;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.Urls;
import it.giacomos.android.wwwsapp.service.PostDataService;

import android.app.Activity;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity implements OnClickListener
{
    private String mLayerName, mAccount;
    private double mLatitude, mLongitude;
    private XmlUiHelper mXmlUIHelper;

    public ReportActivity()
    {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        boolean error = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_activity_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = this.getIntent();
        if (i != null)
        {
            String layer = i.getStringExtra("layer");
            String locality = i.getStringExtra("locality");
            if (locality == null)
                locality = "";
            mLatitude = i.getDoubleExtra("latitude", -1.0);
            mLongitude = i.getDoubleExtra("longitude", -1.0);
            mAccount = i.getStringExtra("account");
            if (layer != null && mLatitude >= 0 && mLongitude >= 0 && mAccount != null && !mAccount.isEmpty())
            {
                mLayerName = layer;
                mXmlUIHelper = new XmlUiHelper(this);
                mXmlUIHelper.addTextPlaceHolder("#locality", locality);
                mXmlUIHelper.build(layer, locality, XmlUiHelper.UI_TYPE_REPORT);

                setTitle(mXmlUIHelper.getTitle());
                Button b = (Button) findViewById(R.id.buttonOk);
                b.setOnClickListener(this);
                b = (Button) findViewById(R.id.buttonCancel);
                b.setOnClickListener(this);
                error = false;
            }
        }
        if(error)
            Log.e("ReportActivity.onCreate", " intent is null or invalid params");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {

        if (view.getId() == R.id.buttonOk)
        {
            /* get device id and registration id */
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            /* Get data from the UI and place it on a HashMap key/value */
            ReportDataBuilder reportDataBuilder = new ReportDataBuilder();
            reportDataBuilder.build(mXmlUIHelper.getData(), this);
            reportDataBuilder.add("account", mAccount);
            reportDataBuilder.add("layer", mLayerName);
            reportDataBuilder.add("latitude", mLatitude);
            reportDataBuilder.add("longitude", mLongitude);
            reportDataBuilder.add("device_id", deviceId);

            Intent service_intent = new Intent(this, PostDataService.class);
            service_intent.putExtra("serviceName", "PublishPostService");
            service_intent.putExtra("params", reportDataBuilder.toString());
            service_intent.putExtra("url", new Urls().reportServiceUrl());

            Log.e("ReportActivity.onClick", "starting service PostDataService");
            startService(service_intent);

            Intent activity_intent = new Intent();
            setResult(Activity.RESULT_OK, activity_intent);
            finish();
        } else if (view.getId() == R.id.buttonCancel)
        {
            setResult(Activity.RESULT_CANCELED, null);
            finish();
        }

    }

    /**
     * This class collects all threads whose cancellation is not directly managed by
     * their creators. Typically, they are tasks that are launched to make a post
     * (publish a report or a report request).
     * Note that their cancellation in cancelAll does not interrupt the task if it
     * is already running, so that the post operation can be fulfilled.
     * Simply it is avoided that when the task completes it invokes a callback on a
     * destroyed activity.
     *
     * @author giacomo
     *
     */
    public static class PostReportAsyncTaskPool
    {
        private static PostReportAsyncTaskPool _instance;

        private ArrayList<AsyncTask<?, ?, ?> > mTasks;


        public static PostReportAsyncTaskPool Instance()
        {
            if(_instance == null)
                _instance = new PostReportAsyncTaskPool();
            return _instance;
        }

        private PostReportAsyncTaskPool()
        {
            mTasks = new ArrayList<AsyncTask<?, ?, ?> >();
        }

        public void registerTask(AsyncTask<?, ?, ?> atask)
        {
            mTasks.add(atask);
        }

        public void unregisterTask(AsyncTask<?, ?, ?> atask)
        {
            mTasks.remove(atask);
        }

        public void cancelAll()
        {
            for(AsyncTask <?, ?, ?> at : mTasks)
            {
                if(at.getStatus() != AsyncTask.Status.FINISHED)
                    at.cancel(false);
            }
            mTasks.clear();
        }

    }
}
