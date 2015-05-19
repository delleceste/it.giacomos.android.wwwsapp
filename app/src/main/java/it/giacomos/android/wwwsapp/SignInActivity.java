package it.giacomos.android.wwwsapp;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SignInActivity extends Activity
implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener
{

	  /* Request code used to invoke sign in user interactions. */
	  private static final int RC_SIGN_IN = 0;

	/**
	 * True if the sign-in button was clicked.  When true, we know to resolve all
	 * issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/**
	 * True if we are in the process of resolving a ConnectionResult
	 */
	private boolean mIntentInProgress;
	

	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		
		 mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(Plus.API)
	        .addScope(Plus.SCOPE_PLUS_PROFILE)
	        .build();

	}

	@Override
	protected void onStart() {
		    super.onStart();
		    mGoogleApiClient.connect();
		  }
	
	@Override
	protected void onStop() {
	    super.onStop();

	    if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	  }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
	  mSignInClicked = false;
	  setResult(RESULT_OK);
	  finish();
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		  if (requestCode == RC_SIGN_IN) {
		    if (responseCode != RESULT_OK) {
		      mSignInClicked = false;
		    }

		    mIntentInProgress = false;

		    if (!mGoogleApiClient.isConnected()) {
		      mGoogleApiClient.reconnect();
		    }
		  }
		}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
		    mSignInClicked = true;
		    mGoogleApiClient.connect();
		  }

		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
	  if (!mIntentInProgress) {
	    if (mSignInClicked && result.hasResolution()) {
	    	mIntentInProgress = true;

	      try {
	        result.startResolutionForResult(this, RC_SIGN_IN);
	        mIntentInProgress = true;
	      } catch (SendIntentException e) {
	        // The intent was canceled before it was sent.  Return to the default
	        // state and attempt to connect to get an updated ConnectionResult.
	        mIntentInProgress = false;
	        mGoogleApiClient.connect();
	      }
	    }
	  }
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
	

}
