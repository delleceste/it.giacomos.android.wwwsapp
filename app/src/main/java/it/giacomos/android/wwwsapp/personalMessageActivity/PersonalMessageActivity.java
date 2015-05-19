/**
 * 
 */
package it.giacomos.android.wwwsapp.personalMessageActivity;

import it.giacomos.android.wwwsapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author giacomo
 *
 */
public class PersonalMessageActivity extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_message);

		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		Intent i = getIntent();
		TextView titleV = (TextView) this.findViewById(R.id.tvPersonalMsgTitle);
		TextView msgV = (TextView) this.findViewById(R.id.tvPersonalMessage);
		TextView dateV = (TextView) this.findViewById(R.id.tvPersonalMessageDate);
		
		Button b = (Button) findViewById(R.id.btClosePersonalActivity);
		b.setOnClickListener(this);
		
		titleV.setText(Html.fromHtml(i.getStringExtra("name")));
		msgV.setText(Html.fromHtml(i.getStringExtra("message")));
		dateV.setText(i.getStringExtra("date"));
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btClosePersonalActivity)
			this.finish();
		
	}
	
	
	
}
