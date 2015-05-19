package it.giacomos.android.wwwsapp;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.floatingactionbutton.FloatingActionButton;
import it.giacomos.android.wwwsapp.gcm.GcmRegistrationManager;
import it.giacomos.android.wwwsapp.interfaceHelpers.MenuActionsManager;
import it.giacomos.android.wwwsapp.interfaceHelpers.TitlebarUpdater;
import it.giacomos.android.wwwsapp.layers.LayerListActivity;
import it.giacomos.android.wwwsapp.locationUtils.LocationInfo;
import it.giacomos.android.wwwsapp.locationUtils.LocationService;
import it.giacomos.android.wwwsapp.network.DownloadStatus;
import it.giacomos.android.wwwsapp.network.state.Urls;
import it.giacomos.android.wwwsapp.network.state.ViewType;
import it.giacomos.android.wwwsapp.news.NewsData;
import it.giacomos.android.wwwsapp.news.NewsFetchTask;
import it.giacomos.android.wwwsapp.news.NewsUpdateListener;
import it.giacomos.android.wwwsapp.personalMessageActivity.PersonalMessageData;
import it.giacomos.android.wwwsapp.personalMessageActivity.PersonalMessageDataDecoder;
import it.giacomos.android.wwwsapp.personalMessageActivity.PersonalMessageDataFetchTask;
import it.giacomos.android.wwwsapp.personalMessageActivity.PersonalMessageManager;
import it.giacomos.android.wwwsapp.personalMessageActivity.PersonalMessageUpdateListener;
import it.giacomos.android.wwwsapp.preferences.*;
import it.giacomos.android.wwwsapp.service.ServiceManager;
import it.giacomos.android.wwwsapp.service.sharedData.ReportNotification;
import it.giacomos.android.wwwsapp.service.sharedData.ReportRequestNotification;
import it.giacomos.android.wwwsapp.widgets.AnimatedImageView;
import it.giacomos.android.wwwsapp.widgets.map.MapFragmentListener;
import it.giacomos.android.wwwsapp.widgets.map.MapMode;
import it.giacomos.android.wwwsapp.widgets.map.MapViewMode;
import it.giacomos.android.wwwsapp.widgets.map.OMapFragment;
import it.giacomos.android.wwwsapp.widgets.map.ReportRequestListener;
import it.giacomos.android.wwwsapp.widgets.map.report.RemovePostConfirmDialog;
import it.giacomos.android.wwwsapp.widgets.map.report.PostActivity;
import it.giacomos.android.wwwsapp.widgets.map.report.ReportRequestDialogFragment;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostActionResultListener;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostReport;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostType;
import it.giacomos.android.wwwsapp.widgets.map.report.tutorialActivity.TutorialPresentationActivity;
import android.support.v4.view.MenuItemCompat;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;


import io.presage.Presage;
import io.presage.utils.IADHandler;

/** 
 * 
 * @author giacomo
 *
 * - download first image (today forecast) and situation text immediately and independently (i.e. in
 *   two AsyncTasks each other), so that the user obtains the first information as soon as possible;
 * - in the background, continue downloading all other relevant information (tomorrow, two days image and
 *   forecast), so that it is ready when the user flips.
 */
public class HelloWorldActivity extends AppCompatActivity 
implements OnClickListener, 
OnMenuItemClickListener, 
OnDismissListener,
MapFragmentListener,
PostActionResultListener,
ReportRequestListener, 
NewsUpdateListener, 
PersonalMessageUpdateListener,
ConnectionCallbacks, 
OnConnectionFailedListener,
ListView.OnItemClickListener,
OnItemSelectedListener /* main spinner */
{
	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/**
	 * True if we are in the process of resolving a ConnectionResult
	 */
	private boolean mIntentInProgress;

	private GoogleApiClient mGoogleApiClient;

	private DownloadStatus mDownloadStatus;

	public HelloWorldActivity()
	{
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//		Log.e("HelloWorldActivity.onCreate", "onCreate called");

		mSettings = new Settings(this);
		this.mDownloadStatus = DownloadStatus.Instance();

		/* create the location update client and connect it to the location service */
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resultCode == ConnectionResult.SUCCESS)
		{
			mGoogleServicesAvailable = true;
			setContentView(R.layout.main);

			if(mSettings.isFirstExecution())
			{
				this.mStartSignInActivity();
			}
			else
			{
				mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_PROFILE)
				.addApi(LocationServices.API)
				.build();
			}

			init();
		}
		else
		{
			mGoogleServicesAvailable = false;
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
		}

		/* if a user has purchased the application using the inApp method, do not
		 * show ads
		 */
		mAdsEnabled = false;
		if(mAdsEnabled)
		{
			Presage.getInstance().setContext(this.getBaseContext());
			Presage.getInstance().start();
		}
	}

	public void onResume()
	{	
		super.onResume();
		//		Log.e("HelloWorldActivity.onResume", "onResume called");
		if(!mGoogleServicesAvailable)
			return;

		/* (re)connect the location update client */
		mLocationService.connect();

		/* remove notifications from the notification bar */
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(ReportRequestNotification.REQUEST_NOTIFICATION_ID);
		mNotificationManager.cancel(ReportNotification.REPORT_NOTIFICATION_ID);

		/*
		 * mAdsEnabled is true if
		 * - not purchased, not first execution of the app
		 * - not resuming after onPause
		 * - resuming when savedInstanceState is null (i.e. don't show ads after screen rotation) 
		 * - not resuming after onNewIntent
		 */
		if(mAdsEnabled) /* not purchased, not executed for the first time */
		{
			Presage.getInstance().adToServe("interstitial", new IADHandler() {

				@Override
				public void onAdNotFound() {
					Log.e("PRESAGE", "ad not found");
				}

				@Override
				public void onAdFound() {
					Log.e("PRESAGE", "ad found");
					mSettings.setAdsShownNow();
				}

				@Override
				public void onAdClosed() {
					Log.e("PRESAGE", "ad closed");
				}
			});
		}
	}

	public void onPause()
	{
		super.onPause();
		if(!mGoogleServicesAvailable)
			return;
		mLocationService.disconnect();

		if(mNewsFetchTask != null && mNewsFetchTask.getStatus() != AsyncTask.Status.FINISHED)
			mNewsFetchTask.cancel(false);

		if(mPersonalMessageDataFetchTask != null && mPersonalMessageDataFetchTask.getStatus() != AsyncTask.Status.FINISHED)
			mPersonalMessageDataFetchTask.cancel(false);

		/* no ads when resuming after onPause */
		mAdsEnabled = false;
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		Log.e("HelloWorldActivity.onPostCreate", " saved InstanceState " + savedInstanceState);

		super.onPostCreate(savedInstanceState);
		if(!mGoogleServicesAvailable)
			return;
		int forceDrawerItem = -1;
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		/* force to switch to Reports mode */
		if(extras != null)
		{
			if(extras.getBoolean("NotificationReportRequest"))
			{
				forceDrawerItem = 5;
				getIntent().removeExtra("NotificationReportRequest");
			}
			if(extras.getBoolean("NotificationReport"))
			{
				forceDrawerItem = 5;
				getIntent().removeExtra("NotificationReport");
			}
			else if(extras.getBoolean("NotificationRainAlert"))
			{
				forceDrawerItem = 1; /* radar */
				getIntent().removeExtra("NotificationRainAlert");
			}
		}

		/* do not show ads when savedInstanceState is not null (e.g. after screen rotation */
		if(savedInstanceState != null)
			mAdsEnabled = false;
	}

	@Override
	public void onConnected(Bundle connectionHint) 
	{
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) 
		{
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			String personName = currentPerson.getDisplayName();
			Image personImage = currentPerson.getImage();
			String account = Plus.AccountApi.getAccountName(mGoogleApiClient);
			Log.e("onConnected", "name: " + personName + " img " + personImage + " mail " + account);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) 
	{
		if (!mIntentInProgress) 
		{
			if (result.hasResolution()) 
			{
				mIntentInProgress = true;
				try {
					result.startResolutionForResult(this, RC_SIGN_IN);
					mIntentInProgress = true;
				} 
				catch (SendIntentException e) {
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

	@Override
	protected void onNewIntent(Intent intent)
	{
		int drawerItem = -1;
		Bundle extras = intent.getExtras();
		/* force to switch to Reports mode */
		if(extras != null)
		{
			if(extras.getBoolean("NotificationReportRequest"))
			{
				drawerItem = mDrawerItems.length - 1;
				getIntent().removeExtra("NotificationReportRequest");
			}
			if(extras.getBoolean("NotificationReport"))
			{
				drawerItem = 5;
				getIntent().removeExtra("NotificationReport");
			}
			else if(extras.getBoolean("NotificationRainAlert"))
			{
				drawerItem = 1; /* radar */
				getIntent().removeExtra("NotificationRainAlert");
			}

			mAdsEnabled = false;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(!mGoogleServicesAvailable)
			return;
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void onStop()
	{
		/* From Android documentation:
		 * Note that this method may never be called, in low memory situations where 
		 * the system does not have enough memory to keep your activity's process running 
		 * after its onPause() method is called. 
		 */
		super.onStop();
		if(!mGoogleServicesAvailable)
			return;
		if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) 
			mGoogleApiClient.disconnect();
	}

	protected void onDestroy()
	{
		if(mGoogleServicesAvailable)
		{
			if(mMapOptionsMenu != null)
				mMapOptionsMenu.dismiss();
		}
		super.onDestroy();
	}

	public void onRestart()
	{
		super.onRestart();
		if(!mGoogleServicesAvailable)
			return;
	}

	public void onStart()
	{
		super.onStart();
		if(!mGoogleServicesAvailable)
			return;
		if(mGoogleApiClient != null) /* first execution */
			mGoogleApiClient.connect();
	}

	public void init()
	{
		mCurrentFragmentId = -1;
		mLastTouchedY = -1;

		/* if it's time to get personal message, wait for network and download it */
		if(!mSettings.timeToGetPersonalMessage() && !mSettings.getPersonalMessageData().isEmpty())
			this.onPersonalMessageUpdate(mSettings.getPersonalMessageData(), true); /* true: fromCache */

		mCurrentViewType = ViewType.HOME;

		mLocationService = new LocationService(getApplicationContext());
		/* Set the number of pages that should be retained to either side of 
		 * the current page in the view hierarchy in an idle state
		 */
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		this.setSupportActionBar(toolbar);

		mProgressBar = (ProgressBar) findViewById (R.id.mainProgressBar);

		mDrawerItems = getResources().getStringArray(R.array.drawer_text_items);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		int[] drawerListIcons = new int[]{ -1, -1, R.drawable.ic_menu_shared};
		ArrayList <HashMap <String, String> > alist = new ArrayList <HashMap <String, String> >();
		for(int i = 0; i < drawerListIcons.length; i++)
		{
			HashMap<String, String> hm = new HashMap<String,String>();
			hm.put("ITEM", mDrawerItems[i]);
			hm.put("ICON", Integer.toString(drawerListIcons[i]));
			alist.add(hm);
		}
		String[] from = { "ITEM", "ICON" };
		int[] to   = { R.id.drawerItemText, R.id.drawerItemIcon };

		/* Set the adapter for the list view */
		mDrawerList.setAdapter(new SimpleAdapter(this, alist, R.layout.drawer_list_item, from, to));

		/* Set the list's click listener */
		mDrawerList.setOnItemClickListener(this);

		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new MyActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
		/*Set the drawer toggle as the DrawerListener */
		drawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mMenuActionsManager = null;
		mCurrentLocation = null;

		/* to show alerts inside onPostResume, after onActivityResult */
		mMyPendingAlertDialog = null;
		mReportConditionsAccepted = mSettings.reportConditionsAccepted();

		/* if there is an application upgrade, the registration id must be regenerated.
		 * The service may not be immediately restarted (the service also requests a new
		 * registration id if necessary), and so it is better to generate a new one if 
		 * the registrationId is empty.
		 */
		GcmRegistrationManager gcmRM = new GcmRegistrationManager();
		String registrationId = gcmRM.getRegistrationId(getApplicationContext());
		if(registrationId.isEmpty())
			gcmRM.registerInBackground(this);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewReport);
		fab.setOnClickListener(this);
		fab.setColor(getResources().getColor(R.color.accent_semitransparent));
		fab.setDrawable(getResources().getDrawable(R.drawable.ic_menu_edit_fab));
		/* hide fab if the user scrolls with his finger down, setting a minimum scroll y length */
		final float floatingActionButtonHideYThresholdDPI = 12.0f;
		final float density = getResources().getDisplayMetrics().density;

		mFloatingActionButtonHideYThreshold = density * floatingActionButtonHideYThresholdDPI;
		getMapFragment().setMapFragmentListener(this);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		/* save refresh state of the refresh animated circular scrollbar in order to 
		 * restore its state and visibility right after the menu is recreated.
		 */
		boolean refreshWasVisible = (mRefreshAnimatedImageView != null && 
				mRefreshAnimatedImageView.getVisibility() == View.VISIBLE);
		mRefreshAnimatedImageView = null;

		mButtonsActionView = MenuItemCompat.getActionView(menu.findItem(R.id.actionbarmenu));
		mRefreshAnimatedImageView = (AnimatedImageView) mButtonsActionView.findViewById(R.id.refresh_animation);
		if(refreshWasVisible)
			mRefreshAnimatedImageView.start();
		mInitButtonMapsOverflowMenu();

		/* set visibility and state on map buttons */
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		this.closeOptionsMenu();
		if(mMenuActionsManager == null)
			mMenuActionsManager = new MenuActionsManager(this);

		boolean ret = mMenuActionsManager.itemSelected(item);
		return ret;
	}

	@Override
	public void onBackPressed()
	{
		if(!mGoogleServicesAvailable)
			super.onBackPressed();
		else
		{
			/* first of all, if there's a info window on the map, close it */
			OMapFragment map = getMapFragment();
			if(mCurrentFragmentId == 1  &&  map.isInfoWindowVisible()) /* map view visible */
			{
				map.hideInfoWindow();
			}
			else if(mCurrentFragmentId == 1)
			{
				mDrawerList.setItemChecked(0, true);
			}
			else
				super.onBackPressed();
		}
	}

	@Override
	public void onCameraReady()
	{
		Intent i = getIntent();
		if(i != null)
		{
			Bundle extras = i.getExtras();
			if(extras != null && extras.containsKey("ptLatitude") && extras.containsKey("ptLongitude") && getMapFragment() != null)
			{
				getMapFragment().moveTo(extras.getDouble("ptLatitude"), extras.getDouble("ptLongitude"));
				i.removeExtra("ptLatitude");
				i.removeExtra("ptLongitude");
			}
		}
	}

	@Override
	public void onGoogleMapReady() {

	}

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(outState != null) /* fix ANR: null pointer */
		{
			Spinner spinner = (Spinner) findViewById(R.id.toolbar_spinner);
			outState.putInt("spinnerPosition", spinner.getSelectedItemPosition());
		}
	}

	protected void onRestoreInstanceState(Bundle inState)
	{
		super.onRestoreInstanceState(inState);
	}

	void radar()
	{
		findViewById(R.id.mapProgressBar).setVisibility(View.VISIBLE);
	}

	/** implemented from PostActionResultListener.
	 * This method is invoked after a http post has been executed and returned, with or without
	 * errors as indicated in the method parameters.
	 */
	@Override
	public void onPostActionResult(boolean error, String message, PostType postType) 
	{
		/* the message in the REQUEST contains the number of users to which the request has been
		 * pushed by means of the Google Cloud Messaging. If > 0 (may be 0 if no users are currently
		 * available in that area or if the present users have recently received a request or published
		 * a report) show a message telling how many users have been pushed, else, if 0, simply tell that
		 * the request was successful.
		 */
		if(!error && postType == PostType.REQUEST && Integer.parseInt(message) > 0)
			Toast.makeText(this, message + " " + getString(R.string.users_poked), Toast.LENGTH_SHORT).show();
		else if(!error && (postType == PostType.REQUEST || postType == PostType.REPORT) )
			Toast.makeText(this, R.string.reportDataSentOk, Toast.LENGTH_SHORT).show();
		else if(error)
		{
			String m = this.getResources().getString(R.string.reportError) + "\n" + message;
			Toast.makeText(this, m, Toast.LENGTH_LONG).show();
		}
		/* invoked when the user has successfully published its request or cancelled one of his reports
		 * also. Since the PostType.REPORT is realized through another Activity, when this activity
		 * is resumed, it performs an update of the report automatically (see onNetworkBecomesAvailable,
		 * which calls MapViewUpdater.
		 */
		if(mCurrentViewType == ViewType.REPORT && postType != PostType.REPORT)
		{
			
		}
	}

	/** implements ReportRequestListener interface
	 * 
	 */
	@Override
	public void onMyReportLocalityChanged(String locality) 
	{
		ReportRequestDialogFragment rrdf = (ReportRequestDialogFragment) 
				getSupportFragmentManager().findFragmentByTag("ReportRequestDialogFragment");
		if(rrdf != null)
			rrdf.setLocality(locality);
	}

	/** implements ReportRequestListener interface
	 * 
	 */
	@Override
	public void onMyReportRequestTriggered(LatLng pointOnMap, String locality) 
	{
		OMapFragment omf = getMapFragment();
		Location myLocation = getLocationService().getCurrentLocation();
		if(myLocation == null || omf.pointTooCloseToMyLocation(myLocation, pointOnMap))
		{
			MyAlertDialogFragment.MakeGenericError(R.string.reportPointTooCloseToMyLocation, this);
		}
		else
		{
			ReportRequestDialogFragment rrdf = ReportRequestDialogFragment.newInstance(locality);
			rrdf.setData(pointOnMap, locality);
			rrdf.show(getSupportFragmentManager(), "ReportRequestDialogFragment");
		}
	}

	/** implements ReportRequestListener.onMyPostRemove method interface
	 * 
	 */
	@Override
	public void onMyPostRemove(LatLng position, PostType type) 
	{
		RemovePostConfirmDialog rrccd = new RemovePostConfirmDialog();
		/* the following in order to be notified when the dialog is cancelled or the remove post 
		 * task has been accomplished, successfully or not, according to the results contained 
		 * in the onPostActionResult() method.
		 */
		rrccd.setPostActionResultListener(this);
		rrccd.setLatLng(position);
		rrccd.setType(type);
		rrccd.setDeviceId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		rrccd.show(getSupportFragmentManager(), "RemovePostConfirmDialog");
	}

	/** implements ReportRequestListener.onMyReportPublish method interface.
	 *  This is invoked when a user touches a baloon on a ReporOverlay request marker.
	 * 
	 */
	public void onMyReportPublish()
	{
		startReportActivity();
	}

	/** implements ReportRequestListener.onMyReportRequestDialogCancelled method interface
	 * 
	 */
	@Override
	public void onMyReportRequestDialogCancelled(LatLng position) 
	{
		
	}

	private void mStartSignInActivity()
	{
		Intent i = new Intent(this, SignInActivity.class);
		this.startActivityForResult(i, SIGN_IN_ACTIVITY_FOR_RESULT_ID);
	}

	private void mStartTutorialActivity()
	{
		Intent i = new Intent(this, TutorialPresentationActivity.class);
		i.putExtra("startedFromMainActivity", true);
		i.putExtra("conditionsAccepted", mReportConditionsAccepted);
		this.startActivityForResult(i, TUTORIAL_ACTIVITY_FOR_RESULT_ID);
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) 
	{
		/* must manually check an unchecked item, and vice versa.
		 * a checkbox or radio button does not change its state automatically.
		 */
		if(menuItem.isCheckable())
			menuItem.setChecked(!menuItem.isChecked());
		OMapFragment omv = getMapFragment();
		switch(menuItem.getItemId())
		{
		case R.id.centerMapButton:
			omv.centerMap();
			break;	
		case R.id.mapNormalViewButton:
			omv.setNormalViewEnabled(menuItem.isChecked());
			break;		
		case R.id.satelliteViewButton:
			omv.setSatEnabled(menuItem.isChecked());
			break;			
		case R.id.terrainViewButton:
			omv.setTerrainEnabled(menuItem.isChecked()); 
			break;
		case R.id.radarInfoButton:
			View radarInfoTextView = findViewById(R.id.radarInfoTextView);
			if(menuItem.isChecked())
				radarInfoTextView.setVisibility(View.VISIBLE);
			else
				radarInfoTextView.setVisibility(View.GONE);
			break;
		case R.id.reportHelpAction:
			/* show map tilt transparent overlay next time too */
			mSettings.setTiltTutorialShown(false);
			mStartTutorialActivity();
			break;
		default:
			break;
		}
		return false;
	}

	private void mStartNotificationService(boolean startService) 
	{
		if(mGoogleApiClient.isConnected())
		{
			ServiceManager serviceManager = new ServiceManager();
			Log.e("HelloWorldActivity.onClick", "enabling service: " + startService +
					" was running "+ serviceManager.isServiceRunning(this));
			boolean ret = serviceManager.setEnabled(this, startService);
				if(ret && startService)
					Toast.makeText(this, R.string.notificationServiceStarted, Toast.LENGTH_LONG).show();
				else if(ret && !startService)
					Toast.makeText(this, R.string.notificationServiceStopped, Toast.LENGTH_LONG).show();
				else if(!ret && startService)
					Toast.makeText(this, R.string.notificationServiceWillStartOnNetworkAvailable, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.actionOverflow) 
		{
			mCreateMapOptionsPopupMenu(true);
		} 
		else if(v.getId() == R.id.fabNewReport)
		{
			mReportConditionsAccepted = mSettings.reportConditionsAccepted();
			if(mReportConditionsAccepted)
				startReportActivity();	
		}
	}

	@Override
	public void onDismiss(PopupMenu popupMenu) 
	{
		/* uncheck button when popup is dismissed */
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		buttonMapsOveflowMenu.setChecked(false);
	} 

	/* called by onPrepareOptionsMenu every time the action bar is recreated
	 * @param buttonsActionView the button (view) where the menu is anchored.
	 * 
	 */
	private void mInitButtonMapsOverflowMenu() 
	{
		if(mButtonsActionView == null || !mGoogleServicesAvailable)
			return;

		/* button for maps menu */
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		switch(mCurrentViewType)
		{
		case HOME:
		case TODAY:
		case TOMORROW:
		case TWODAYS:
		case THREEDAYS:
		case FOURDAYS:
			if(buttonMapsOveflowMenu != null)
				buttonMapsOveflowMenu.setVisibility(View.GONE);
			break;
		default:
			buttonMapsOveflowMenu.setOnClickListener(this);
			buttonMapsOveflowMenu.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void mCreateMapOptionsPopupMenu(boolean show) 
	{
		OMapFragment map = getMapFragment();
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		mMapOptionsMenu = new PopupMenu(this, buttonMapsOveflowMenu);
		Menu menu = mMapOptionsMenu.getMenu();
		mMapOptionsMenu.getMenuInflater().inflate(R.menu.map_options_popup_menu, menu);
		/* report action */
		menu.findItem(R.id.reportUpdateAction).setVisible(mCurrentViewType == ViewType.REPORT);
		/* tutorial */
		menu.findItem(R.id.reportHelpAction).setVisible(mCurrentViewType == ViewType.REPORT);

		switch(mCurrentViewType)
		{
		case HOME: case TODAY: case TOMORROW: case TWODAYS: case THREEDAYS: case FOURDAYS:
			menu.findItem(R.id.satelliteViewButton).setVisible(false);
			menu.findItem(R.id.terrainViewButton).setVisible(false);
			menu.findItem(R.id.mapNormalViewButton).setVisible(false);
			menu.findItem(R.id.centerMapButton).setVisible(false);
			break;
		default:
			menu.findItem(R.id.satelliteViewButton).setVisible(true);
			menu.findItem(R.id.terrainViewButton).setVisible(true);
			menu.findItem(R.id.mapNormalViewButton).setVisible(true);
			menu.findItem(R.id.centerMapButton).setVisible(true);
			menu.findItem(R.id.satelliteViewButton).setChecked(map.isSatEnabled());
			menu.findItem(R.id.terrainViewButton).setChecked(map.isTerrainEnabled());
			menu.findItem(R.id.mapNormalViewButton).setChecked(map.isNormalViewEnabled());
			break;
		}

		if(show)
		{
			mMapOptionsMenu.setOnMenuItemClickListener(this);
			mMapOptionsMenu.setOnDismissListener(this);
			mMapOptionsMenu.show();
		}
	}

	public ViewType getCurrentViewType()
	{
		return mCurrentViewType;
	}

	public int getDisplayedFragment() {
		return mCurrentFragmentId;
	}

	public void showFragment(int id)
	{
		OMapFragment mapF = this.getMapFragment();
		//ForecastTabbedFragment forecastFrag = getForecastFragment();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(id == 0)
		{
			ft.hide(mapF);
		//	ft.show(forecastFrag);
		}
		else
		{
		//	ft.hide(forecastFrag);
			ft.show(mapF);
		}
		ft.commit();
		mCurrentFragmentId = id;
	}

	public void switchView(ViewType id) 
	{
		mCurrentViewType = id;
		OMapFragment mapFragment = getMapFragment();
		showFragment(1);
		
		if (id == ViewType.ACTION_CENTER_MAP) 
		{
			mapFragment.centerMap();
		}

		TitlebarUpdater titleUpdater = new TitlebarUpdater();
		titleUpdater.update(this);
		titleUpdater = null;

		/* show or hide maps menu button according to the current view type */
		mInitButtonMapsOverflowMenu();

		/* hide fab if in observations, radar or webcam mode */
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewReport);
		if( /* fab.isVisible() && */ (this.mCurrentFragmentId == 1 && id != ViewType.REPORT))
			fab.hide(true);
		else if(!fab.isVisible())
			fab.hide(false);
		else if(mCurrentFragmentId == 1)
			fab.setColor(this.getResources().getColor(R.color.accent));
		else
			fab.setColor(getResources().getColor(R.color.accent_semitransparent));
	}

	public void startReportActivity()
	{
		Location loc = this.mLocationService.getCurrentLocation();
		LocationInfo loci = mLocationService.getCurrentLocationInfo();
		if(loc == null)
			MyAlertDialogFragment.MakeGenericError(R.string.location_not_available, this, 
					MyAlertDialogFragment.OPTION_OPEN_GEOLOCALIZATION_SETTINGS);
		else if(!mDownloadStatus.isOnline)
			MyAlertDialogFragment.MakeGenericError(R.string.reportNeedToBeOnline, this, 
					MyAlertDialogFragment.OPTION_OPEN_NETWORK_SETTINGS);
		else
		{
		
			Intent i = new Intent(this, PostActivity.class);
			if(loci != null)
			{
				i.putExtra("locality", loci.locality);
			}
			this.startActivityForResult(i, REPORT_ACTIVITY_FOR_RESULT_ID);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == SIGN_IN_ACTIVITY_FOR_RESULT_ID)
		{
			mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_PROFILE)
			.build();

			if(!mGoogleApiClient.isConnecting())
			{
				Log.e("onActivityResult", "not already connecting to mGoogleApiClient........");
				mGoogleApiClient.connect();
			}
		}

		if (requestCode == RC_SIGN_IN) 
		{
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnected()) {
				mGoogleApiClient.reconnect();
			}
		}

		//		Log.e("onActivityResult", "result " + resultCode);
		if(requestCode == REPORT_ACTIVITY_FOR_RESULT_ID)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				Bundle extras = data.getExtras();
				String locality = extras.getString("locality");

				Location reportLocation = new Location("");
				reportLocation.setLatitude(data.getDoubleExtra("latitude", 0));
				reportLocation.setLongitude(data.getDoubleExtra("longitude", 0));
				GcmRegistrationManager gcmRM = new GcmRegistrationManager();

				/* ok */
				String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
				String registrationId = gcmRM.getRegistrationId(getApplicationContext());
				Log.e("onActivityResult", " my reg id is " + registrationId);
				new PostReport(data.getStringExtra("user"), deviceId, registrationId,
						locality, reportLocation.getLatitude(), reportLocation.getLongitude(), 
						data.getIntExtra("sky", 0), data.getIntExtra("wind", 0), 
						data.getStringExtra("temperature"), data.getStringExtra("comment"), this);
			}
		}
		else if(requestCode == TUTORIAL_ACTIVITY_FOR_RESULT_ID)
		{
			//			Log.e("HelloWorldActivity.onActivityResult", "resultCode " + resultCode + " data " + data + " OK " +
			//					RESULT_OK + " cancelled " + RESULT_CANCELED);
			boolean conditionsAccepted = false;
			if(data != null)
				conditionsAccepted = data.getBooleanExtra("conditionsAccepted", false);
			if(conditionsAccepted != mReportConditionsAccepted)
			{
				mReportConditionsAccepted = conditionsAccepted;
				mSettings.setReportConditionsAccepted(conditionsAccepted);
				mStartNotificationService(conditionsAccepted && mSettings.notificationServiceEnabled());
			}

			if(conditionsAccepted)
			{
			}
			else
			{
				
			}
		}
	}

	public void makePendingAlertErrorDialog(String error)
	{
		mMyPendingAlertDialog = new MyPendingAlertDialog(MyAlertDialogType.ERROR,  error);
	}

	@Override
	public void onPostResume()
	{
		super.onPostResume();
		if(mMyPendingAlertDialog != null && mMyPendingAlertDialog.isShowPending())
			mMyPendingAlertDialog.showPending(this);
	}

	public Location getCurrentLocation()
	{
		return mCurrentLocation;
	}

	public  String[] getDrawerItems()
	{
		return mDrawerItems;
	}

	public AnimatedImageView getRefreshAnimatedImageView()
	{
		return mRefreshAnimatedImageView;
	}

	public DownloadStatus getDownloadStatus()
	{
		return mDownloadStatus;
	}

	public LocationService getLocationService()
	{
		return mLocationService;
	}

	public OMapFragment getMapFragment()
	{
		OMapFragment mapFrag = (OMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapview);
		return mapFrag;
	}

	@Override
	public void onNewsUpdateAvailable(NewsData newsData) 
	{
		Log.e("HelloWorldActivity", "onNewsUpdateAvailable " + System.currentTimeMillis());
		mSettings.setNewsReadNow();
		mSettings.setNewsFetchedNow();
		/* post a notification */
		String url = newsData.getUrl();
		int ledColor = Color.argb(255, 255, 0, 0); /* cyan notification */
		String text = newsData.getText();
		Intent newsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		int iconId = R.drawable.ic_launcher_statusbar_message;
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int notificationFlags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(iconId)
		.setAutoCancel(true)
		.setContentTitle(getResources().getString(R.string.news_alert_title))
		.setContentText(text).setDefaults(notificationFlags);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(HelloWorldActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(newsIntent);

		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationBuilder.setContentIntent(resultPendingIntent);
		notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		// mId allows you to update the notification later on.

		Notification notification = notificationBuilder.build();
		notification.ledARGB = ledColor;
		notification.ledOnMS = 800;
		notification.ledOffMS = 2200;
		notificationManager.notify("NOTIFICATION_NEWS", 13799,  notification);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) 
	{
		if(mCurrentFragmentId == 0)
		{
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewReport);
			float y = event.getY();
			//Log.e("HelloWorldActivity.dispatchTouchEvent", " lat tou" + mLastTouchedY + " y " + y);
			/* hide if the user scrolls down along y for at least mFloatingActionButtonHideYThreshold pixels */
			if(mLastTouchedY >= 0 && y < mLastTouchedY - mFloatingActionButtonHideYThreshold)
				fab.hide(true);
			else if(y > mLastTouchedY + mFloatingActionButtonHideYThreshold)
				fab.hide(false);
			if(event.getAction() == MotionEvent.ACTION_DOWN)
				mLastTouchedY = y;
			else if(event.getAction() == MotionEvent.ACTION_UP)
				mLastTouchedY = -1f;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void onPersonalMessageUpdate(String d, boolean fromCache) 
	{
		if(!d.isEmpty() && d.compareTo(mSettings.getPersonalMessageData()) != 0)
			mSettings.setPersonalMessageData(d);
		if(!fromCache)
			mSettings.setPersonalMessageDownloadedNow();
		PersonalMessageData data = new PersonalMessageDataDecoder(d).getData();
		//		Log.e("HelloWorldActivity.onPersonalMessageUpdate", "raw data \"" + d + "\"");
		if(data.isValid()) /* name, message, date must be not empty */
			new PersonalMessageManager(this, data);
	}

	public void shareMeteoFVGApp() 
	{
		String appUrl; /* free or pro */
		appUrl = new Urls().getAppStoreUrl();
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
		sendIntent.setType("text/plain");
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.send_meteofvgapp_subject));
		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_meteofvgapp_to)));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String[] drawerItems = getDrawerItems();
		
		if(position < 1) /* map */
		{
			mDrawerList.setItemChecked(position, true);
			setTitle(drawerItems[position]);
		}
		else if(position == 1)
		{
			Intent i = new Intent(this, LayerListActivity.class);
			this.startActivityForResult(i, LAYER_LIST_ACTIVITY_FOR_RESULT_ID);
		}
		
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.closeDrawer(mDrawerList);		
	}
	

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	/* private members */
	private Location mCurrentLocation;
	private MenuActionsManager mMenuActionsManager;
	private Settings mSettings;

	private ListView mDrawerList;
	private String[] mDrawerItems;
	private MyActionBarDrawerToggle mDrawerToggle;
	private AnimatedImageView mRefreshAnimatedImageView;
	private ViewType mCurrentViewType;
	/* ActionBar menu button and menu */
	private View mButtonsActionView;
	private boolean mGoogleServicesAvailable;
	Urls m_urls;
	private LocationService mLocationService;
	private PopupMenu mMapOptionsMenu;

	private NewsFetchTask mNewsFetchTask;
	private PersonalMessageDataFetchTask mPersonalMessageDataFetchTask;

	private boolean mReportConditionsAccepted;
	private boolean mAdsEnabled;

	RelativeLayout mMainLayout;

	public static final int REPORT_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 100;
	public static final int TUTORIAL_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 101;
	public static final int SETTINGS_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 102;
	private static final int SIGN_IN_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 103;
	private static final int LAYER_LIST_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 104;

	private MyPendingAlertDialog mMyPendingAlertDialog;

	int availCnt = 0;

	int mCurrentFragmentId;
	private float mLastTouchedY;
	private float mFloatingActionButtonHideYThreshold;

	private ProgressBar mProgressBar;
}
