package it.giacomos.android.wwwsapp.report;

import it.giacomos.android.wwwsapp.R;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ReportActivity extends AppCompatActivity implements OnClickListener
{
    private String mLocality;
    private double mLatitude, mLongitude;
    private ReportUiHelper mReportUIHelper;

    public ReportActivity()
    {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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
            if (layer != null && mLatitude >= 0 && mLongitude >= 0)
            {
                mReportUIHelper = new ReportUiHelper(this);
                mReportUIHelper.addTextPlaceHolder("#locality", locality);
                mReportUIHelper.build(layer, locality);
            }
        }
        else
            Log.e("onCreate", " intent is null");
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
            Intent intent = new Intent();
            intent.putExtra("comment", "-");
            intent.putExtra("latitude", mLatitude);
            intent.putExtra("longitude", mLongitude);

            ReportDataBuilder reportDataBuilder = new ReportDataBuilder();
            reportDataBuilder.build(mReportUIHelper.getData(), this);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (view.getId() == R.id.buttonCancel)
        {
            setResult(Activity.RESULT_CANCELED, null);
            finish();
        }

    }

}
