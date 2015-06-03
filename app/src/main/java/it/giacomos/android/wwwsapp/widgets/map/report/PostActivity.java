package it.giacomos.android.wwwsapp.widgets.map.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import it.giacomos.android.wwwsapp.MyAlertDialogFragment;
import it.giacomos.android.wwwsapp.layers.FileUtils;
import it.giacomos.android.wwwsapp.IconTextSpinnerAdapter;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.LayerItemData;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PostActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener
{
    private String mLocality;
    private double mLatitude, mLongitude;
    private HashMap<Integer, Integer> mOptionViewsHash;

    public PostActivity()
    {
        super();
        mOptionViewsHash = null;
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
                mOptionViewsHash = new ReportUiBuilder(this).build(layer, locality);
            }
        }

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

            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (view.getId() == R.id.buttonCancel)
        {
            setResult(Activity.RESULT_CANCELED, null);
            finish();
        }

    }

    private void setEnabled(boolean en)
    {


    }

    private void initByLocation(String temp, int sky, int wind)
    {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id)
    {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @Override
    public void onCheckedChanged(CompoundButton checkbox, boolean isChecked)
    {
        int viewId = this.mOptionViewsHash.get(checkbox.getId());
        findViewById(viewId).setEnabled(isChecked);
    }
}
