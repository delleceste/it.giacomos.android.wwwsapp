package it.giacomos.android.wwwsapp.widgets.map.report;

import java.util.HashMap;

import it.giacomos.android.wwwsapp.widgets.map.report.IconTextSpinnerAdapter;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.preferences.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class PostActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener
{
	private double mLatitude, mLongitude;
	private String mLocality;
	private int mOptionCheckBoxCount;
	private final int OPTION_CB_ID = 1126332445;
	private HashMap<Integer, Integer> mOptionViewsHash;
	
	public PostActivity()
	{
		super();
		mOptionCheckBoxCount = 0;
		mOptionViewsHash = new HashMap<Integer, Integer> ();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.post_activity_layout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		String [] values =   new String[1];
		values[0] = "test";
		addElement("EditText", "Prova",  values, false, 100);
		values = new String[3];
		values[0] = "Valore 1";
		values[1] = "Valore 2";
		values[2] = "Valore 3";
		addElement("Spinner", "Spinner test", values, false, 101);
		values = new String[3];
		values[0] = "Valore 1 optional";
		values[1] = "Valore 2 optional";
		values[2] = "Valore 3 optional";
		addElement("Spinner", "Spinner test option", values, true, 102);
		values = new String[1];
		values[0] = "OK";
		addElement("Button", "Button text", values, false, 103);
		
	}

	private void addElement(String type, String name, String[] values, boolean isOption, int id)
	{
		LinearLayout container = (LinearLayout) findViewById(R.id.containerLayout);
		LinearLayout lo = new LinearLayout(this);
		lo.setOrientation(LinearLayout.HORIZONTAL); /* items displayed in a row */
		lo.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		container.addView(lo);
		CheckBox cb = null;
		/* text label or checkbox for options */

		View nameView = null;
		if(isOption)
		{
			cb = new CheckBox(this);
			cb.setId(OPTION_CB_ID + mOptionCheckBoxCount);
			cb.setText(name);
			cb.setChecked(false);
			mOptionViewsHash.put(cb.getId(), id);
			cb.setOnCheckedChangeListener(this);
			mOptionCheckBoxCount++;
			lo.addView(cb);
			nameView = cb;
		}
		else
		{
			TextView label = new TextView(this);
			label.setText(name);
			lo.addView(label);
			nameView = label;
		}
		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
		nameView.setLayoutParams(lp);
		LayoutParams lp2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2.0f);
		
		/* widget */
		if(type.compareTo("EditText") == 0)
		{
			EditText editText = new EditText(this);
			editText.setId(id);
			if(values != null && values.length > 0)
				editText.setText(values[0]);
			editText.setLayoutParams(lp2);
			lo.addView(editText);
		}
		else if(type.compareTo("Spinner") == 0)
		{
			Spinner spin = new Spinner(this);			
			IconTextSpinnerAdapter adapter = 
					new IconTextSpinnerAdapter(this, 
							R.layout.post_icon_text_spinner, 
							values, this);
			spin.setId(id);
			spin.setAdapter(adapter);
			spin.setLayoutParams(lp2);
			lo.addView(spin);
		}
		else if(type.compareTo("Button") == 0 && values != null && values.length > 0)
		{
			Button b = new Button(this);			
			b.setText(values[0]);
			b.setOnClickListener(this);
			b.setId(id);
			b.setLayoutParams(lp2);
			lo.addView(b);
		}
		
		if(cb != null)
			findViewById(id).setEnabled(cb.isChecked());
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onClick(View view)
	{

		if(view.getId() == R.id.buttonOk)
		{
			Intent intent = new Intent();
			intent.putExtra("comment", "-");
			intent.putExtra("latitude", mLatitude);
			intent.putExtra("longitude", mLongitude);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}


		else if(view.getId() == R.id.buttonCancel)
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
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) 
	{
		int viewId = this.mOptionViewsHash.get(checkbox.getId());
		findViewById(viewId).setEnabled(isChecked);
	}
}
