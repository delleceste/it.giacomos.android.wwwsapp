package it.giacomos.android.wwwsapp.preferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Settings 
{
	public static final int TRIAL_DAYS = 20;
	public static final String PREFERENCES_NAME = "Osmer.conf";
	private SharedPreferences mSharedPreferences;

	public Settings(Context ctx)
	{
		mSharedPreferences = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public boolean isSwipeHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_SWIPE", true);
		return res;
	}

	public void setSwipeHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_SWIPE", en);
		e.commit();
	}

	public boolean hasMeasureMarker1LatLng()
	{
		return mSharedPreferences.contains("MEASURE_MARKER_LAT1");
	}

	public void setMeasureMarker1LatLng(float lat1, float long1)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MEASURE_MARKER_LAT1", lat1);
		e.putFloat("MEASURE_MARKER_LONG1", long1);
		e.commit();
	}

	public float[] getMeasureMarker1LatLng()
	{
		float [] res = new float[2];
		res[0] = mSharedPreferences.getFloat("MEASURE_MARKER_LAT1", -1.0f);
		res[1] = mSharedPreferences.getFloat("MEASURE_MARKER_LONG1", -1.0f);
		return res;
	}

	public void setMapType(int mapType)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putInt("MAP_TYPE", mapType);
		e.commit();
	}

	public int getMapType()
	{
		return mSharedPreferences.getInt("MAP_TYPE", GoogleMap.MAP_TYPE_NORMAL);
	}

	public boolean isMapMoveToMeasureHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_MOVE_P1_TO_MEASURE", true);
		return res;
	}

	public void setMapMoveToMeasureHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_MOVE_P1_TO_MEASURE", en);
		e.commit();
	}

	public boolean isMapMarkerHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_MARKER", true);
		return res;
	}

	public void setMapMarkerHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_MARKER", en);
		e.commit();
	}	

	public boolean isObsScrollIconsHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_OBS_SCROLL_ICONS", true);
		return res;
	}

	public void setObsScrollIconsHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_OBS_SCROLL_ICONS", en);
		e.commit();
	}

	public boolean hasMapWebcamMarkerFontSize()
	{
		return mSharedPreferences.contains("MAP_WEBCAM_MARKER_FONT_SIZE");
	}

	public float mapWebcamMarkerFontSize()
	{
		float res = mSharedPreferences.getFloat("MAP_WEBCAM_MARKER_FONT_SIZE", 21);
		return res;
	}

	public void setMapWebcamMarkerFontSize(float size) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MAP_WEBCAM_MARKER_FONT_SIZE", size);
		e.commit();
	}

	public long getWebcamLastUpdateTimestampMillis()
	{
		return mSharedPreferences.getLong("WEBCAM_LAST_UPDATE_TIMESTAMP_MILLIS", 0);
	}

	public void setWebcamLastUpdateTimestampMillis(long timestampMillis)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("WEBCAM_LAST_UPDATE_TIMESTAMP_MILLIS", timestampMillis);
		e.commit();
	}

	public boolean hasObservationsMarkerFontSize()
	{
		return mSharedPreferences.contains("MAP_OBSERVATIONS_MARKER_FONT_SIZE");
	}

	public float observationsMarkerFontSize()
	{
		float res = mSharedPreferences.getFloat("MAP_OBSERVATIONS_MARKER_FONT_SIZE", 25.0f);
		return res;
	}

	public void setObservationsMarkerFontSize(float size) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MAP_OBSERVATIONS_MARKER_FONT_SIZE", size);
		e.commit();
	}

	public void setMapClickOnBaloonImageHintEnabled(boolean b) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_CLICK_ON_BALOON_IMAGE", b);
		e.commit();
	}

	public boolean isMapClickOnBaloonImageHintEnabled() 
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_CLICK_ON_BALOON_IMAGE", true);
		return res;
	}

	public void saveMapCameraPosition(CameraPosition pos)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("CameraZoom", pos.zoom);
		e.putFloat("CameraTargetLatitude", (float) pos.target.latitude);
		e.putFloat("CameraTargetLongitude", (float) pos.target.longitude);
		e.putFloat("CameraBearing", pos.bearing);
		e.putFloat("CameraTilt", pos.tilt);
		e.commit();
	}


	public void setRadarImageTimestamp(long currentTimeMillis) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("RADAR_IMAGE_TIMESTAMP", currentTimeMillis);
		e.commit();
	}

	public long getRadarImageTimestamp()
	{
		long radarImageTs;
		/* try/catch to correct a previous release which used to put a float 
		 * instead of a long.
		 */
		try
		{
			radarImageTs = mSharedPreferences.getLong("RADAR_IMAGE_TIMESTAMP", 0L);
		}
		catch(ClassCastException cce)
		{
			/* put a long for next time */
			SharedPreferences.Editor e = mSharedPreferences.edit();
			e.putLong("RADAR_IMAGE_TIMESTAMP", 0L);
			e.commit();
			/* and return a 0 timestamp. This will force an update of the radar image */
			radarImageTs = 0;
		}
		return radarImageTs;
	}

	/** returns true if this is the first execution. Then sets the first execution flag to 
	 * false for the next invocations
	 * @return true/false
	 */
	public boolean isFirstExecution()
	{
		boolean ret = mSharedPreferences.getBoolean("IS_FIRST_EXECUTION", true);
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("IS_FIRST_EXECUTION", false);
		e.commit();
		return ret;
	}

	public CameraPosition getCameraPosition()
	{
		CameraPosition pos;
		/* check at least 3 keys to be sure all the parameters have been saved */
		if(mSharedPreferences.contains("CameraTargetLatitude") && mSharedPreferences.contains("CameraTargetLongitude")
				&& mSharedPreferences.contains("CameraTilt"))
		{
			LatLng latLng = new LatLng((double) mSharedPreferences.getFloat("CameraTargetLatitude", -1.0f), 
					(double) mSharedPreferences.getFloat("CameraTargetLongitude", -1.0f));
			pos = new CameraPosition.Builder().
					target(latLng).
					zoom(mSharedPreferences.getFloat("CameraZoom", -1.0f)).
					bearing(mSharedPreferences.getFloat("CameraBearing",-1.0f)).
					tilt(mSharedPreferences.getFloat("CameraTilt", -1.0f)).
					build();
			return pos;
		}
		return null;
	}

	public void setMapWithForecastImageTextFontSize(float fontSize)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MAP_WITH_FOREACAST_IMAGE_TEXT_FONT_SIZE", fontSize);
		e.commit();
	}

	public float getMapWithForecastImageTextFontSize()
	{
		return mSharedPreferences.getFloat("MAP_WITH_FOREACAST_IMAGE_TEXT_FONT_SIZE", 100.0f);
	}

	public void setReporterUserName(String name)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putString("REPORTER_USER_NAME", name);
		e.commit();
	}

	public String getReporterUserName() 
	{
		String res = mSharedPreferences.getString("REPORTER_USER_NAME", "");
		return res;
	}

	public long minTimeBetweenNotificationsMinutes(String tag)
	{
		long res;
		if(tag.contains("RainNotificationTag"))
			res = 30L;
		else
			res = 5L;
		try /* if saved as long */
		{ 
			res = mSharedPreferences.getLong("MIN_TIME_BETWEEN_NOTIFICATIONS_" + tag, res);
		}
		catch(ClassCastException e)
		{
			/* Preferences saves as string... because we edit with a text edit */
			String s = mSharedPreferences.getString("MIN_TIME_BETWEEN_NOTIFICATIONS_" + tag, String.valueOf(res));
			res = Long.parseLong(s);
		}
		return res;
	}

	public void setMinTimeBetweenNotificationsMinutes(String tag, long minTime)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("MIN_TIME_BETWEEN_NOTIFICATIONS_" + tag, minTime);
		e.commit();
	}

	public void saveMyReportRequestMarkerAttributes(double latitude,
			double longitude) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MY_REPORT_REQUEST_MARKER_LAT", (float)latitude);
		e.putFloat("MY_REPORT_REQUEST_MARKER_LONG", (float)longitude);
		e.putLong("MY_REPORT_REQUEST_TIME_MILLIS", System.currentTimeMillis());
		e.commit();

	}

	/** If marker older than 3 hours, throw it away */
	public double getMyReportRequestMarkerLatitude()
	{
		long lastRequestTSMillis = mSharedPreferences.getLong("MY_REPORT_REQUEST_TIME_MILLIS", 0);
		if(System.currentTimeMillis() - lastRequestTSMillis > 3 * 60 * 60 * 1000)
			return -1.0;
		float lat =  mSharedPreferences.getFloat("MY_REPORT_REQUEST_MARKER_LAT", -1.0f);
		return (double) lat;
	}

	public double getMyReportRequestMarkerLongitude()
	{
		long lastRequestTSMillis = mSharedPreferences.getLong("MY_REPORT_REQUEST_TIME_MILLIS", 0);
		if(System.currentTimeMillis() - lastRequestTSMillis > 3 * 60 * 60 * 1000)
			return -1.0;
		float lat =  mSharedPreferences.getFloat("MY_REPORT_REQUEST_MARKER_LONG", -1.0f);
		return (double) lat;
	}

	/** the service sleep interval in milliseconds between two subsequent updates of the 
	 * data on the web server. Defaults to four minutes.
	 * @return
	 */
	public long getServiceSleepIntervalMillis() 
	{		
		long intmin = 5L;
		try /* if saved as long */
		{ 
			intmin = mSharedPreferences.getLong("SERVICE_SLEEP_INTERVAL_MINS",  5L);
		}
		catch(ClassCastException e)
		{
			/* Preferences saves as string... because we edit with a text edit */
			String s = mSharedPreferences.getString("SERVICE_SLEEP_INTERVAL_MINS", "5");
			intmin = Long.parseLong(s);
		}

		return intmin * 60 * 1000;
	}

	public boolean notificationServiceEnabled() 
	{
		boolean ret = mSharedPreferences.getBoolean("NOTIFICATION_SERVICE_ENABLED", true);
		return ret;
	}

	public void setNotificationServiceEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("NOTIFICATION_SERVICE_ENABLED", en);
		e.commit();
	}

	public boolean reportConditionsAccepted()
	{
		boolean ret = mSharedPreferences.getBoolean("REPORT_CONDITIONS_ACCEPTED_V_2_6_1", false);
		return ret;
	}

	public void setReportConditionsAccepted(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("REPORT_CONDITIONS_ACCEPTED_V_2_6_1", en);
		e.commit();
	}

	public long getLastReportDataServiceStartedTimeMillis() 
	{
		long intmillis =  mSharedPreferences.getLong("LAST_REPORT_DATA_SERVICE_STARTED_TIME_MILLIS", 0);
		return intmillis;
	}

	public void setLastReportDataServiceStartedTimeMillis(long timeMillis)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("LAST_REPORT_DATA_SERVICE_STARTED_TIME_MILLIS", timeMillis);
		e.commit();
	}

	public boolean tiltTutorialShown() 
	{
		boolean ret = mSharedPreferences.getBoolean("TILT_TUTORIAL_SHOWN", false);
		return ret;
	}

	public void setTiltTutorialShown(boolean b)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("TILT_TUTORIAL_SHOWN", b);
		e.commit();
	}

	public void setTrialDaysLeft(int daysLeft) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putInt("TRIAL_DAYS_LEFT", daysLeft);
		e.commit();
	}

	public void setApplicationPurchased(boolean purchaseth) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("APPLICATION_PURCHASED", purchaseth);
		e.commit();
	}

	public boolean rainNotificationEnabled() {
		return mSharedPreferences.getBoolean("RAIN_NOTIFICATION_ENABLED", true);
	}

	public boolean useInternalRainDetection() {

		return mSharedPreferences.getBoolean("USE_INTERNAL_RAIN_DETECTION", true);	
	}

	public boolean importantDialogToShow() {
		return mSharedPreferences.getBoolean("IMPORTANT_DIALOG_TO_SHOW_2_6_7_alpha2", true);
	}

	public void setImportantDialogToShow(boolean show) {
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("IMPORTANT_DIALOG_TO_SHOW_2_6_7_alpha2", show);
		e.commit();
	}

	public String getGcmRegistrationId() 
	{
		return mSharedPreferences.getString("GCM_REGISTRATION_ID", "");
	}

	public void saveRegistrationId(String regId, Context context)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putString("GCM_REGISTRATION_ID", regId);
		try {
			e.putInt("LAST_GCM_REGISTERED_APP_VERSION", 
					context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
		} catch (NameNotFoundException e1) {

		}
		e.commit();
	}

	public int getLastGCMRegisteredAppVersionId() 
	{
		return mSharedPreferences.getInt("LAST_GCM_REGISTERED_APP_VERSION", Integer.MIN_VALUE);
	}

	public boolean timeToFetchNews() 
	{
		long lastFetched = mSharedPreferences.getLong("LAST_NEWS_FETCHED_TS", 0);
		long now = System.currentTimeMillis();

		return (now - lastFetched) >  24 * 60 *  60 * 1000;
	}

	public long lastNewsReadTimestamp() {

		return mSharedPreferences.getLong("LAST_NEWS_READ_TS", 0L) / 1000;
	}

	public void setNewsReadNow()
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("LAST_NEWS_READ_TS", System.currentTimeMillis());
		e.commit();
	}

	public void setNewsFetchedNow() 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("LAST_NEWS_FETCHED_TS", System.currentTimeMillis());
		e.commit();
	}

	public long lastRadarImageSyncRequestTimestampMillis() {
		return mSharedPreferences.getLong("LAST_RADAR_IMAGE_SYNC_REQUEST", 60 * 60 * 1000L);
	}

	public void setLastRadarImageSyncRequestedNow()
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("LAST_RADAR_IMAGE_SYNC_REQUEST", System.currentTimeMillis());
		e.commit();
	}

	public void setCurrentTouchedPoint(float x, float y) {
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("FORECAST_MAP_PRESSED_X", x);
		e.putFloat("FORECAST_MAP_PRESSED_Y", y);
		e.commit();
	}

	public void setOnLongPress(boolean longPress) {
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("FORECAST_MAP_LONG_PRESS", longPress);
		e.commit();
	}

	public float getCurrentXTouchedPointNormalized() {
		return mSharedPreferences.getFloat("FORECAST_MAP_PRESSED_X", -1.0f);
	}

	public float getCurrentYTouchedPointNormalized() {
		return mSharedPreferences.getFloat("FORECAST_MAP_PRESSED_Y", -1.0f);
	}

	public boolean getOnLongPressed() {
		return mSharedPreferences.getBoolean("FORECAST_MAP_LONG_PRESS", false);
	}

	public void setCurrentDownPoint(float x, float y) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("FORECAST_MAP_DOWN_X", x);
		e.putFloat("FORECAST_MAP_DOWN_Y", y);
		e.commit();	
	}
	public float getCurrentXDownPointNormalized() {
		return mSharedPreferences.getFloat("FORECAST_MAP_DOWN_X", -1.0f);
	}

	public float getCurrentYDownPointNormalized() {
		return mSharedPreferences.getFloat("FORECAST_MAP_DOWN_Y", -1.0f);
	}
	
	public void setLastDbz(float ldbz)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("RAIN_DETECT_LAST_DBZ", ldbz);
		e.commit();	
	}

	public float getLastDbz() {
		return mSharedPreferences.getFloat("RAIN_DETECT_LAST_DBZ", 0.0f);
	}
	
	public boolean timeToGetPersonalMessage()
	{
		long lastRead = mSharedPreferences.getLong("PERSONAL_MSG_CONF_READ_ON", 0L);
		long now = System.currentTimeMillis();
		return (now - lastRead) >  24 * 60 *  60 * 1000;
	}
	
	public void setPersonalMessageDownloadedNow()
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("PERSONAL_MSG_CONF_READ_ON", System.currentTimeMillis());
		e.commit();
	}
	
	public void setPersonalMessageData(String conf)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putString("PERSONAL_MSG_CONF", conf);
		e.commit();
	}
	
	public String getPersonalMessageData()
	{
		return mSharedPreferences.getString("PERSONAL_MSG_CONF", "");
	}

	public int getInAppPurchaseStatus() {
		return mSharedPreferences.getInt("IN_APP_PURCHASE_STATUS", -1);
	}
	
	public void setInAppPurchased(boolean purchased)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		if(purchased)
			e.putInt("IN_APP_PURCHASE_STATUS", 1);
		else
			e.putInt("IN_APP_PURCHASE_STATUS", 0);
		e.commit();
	}

	/* after 7 hours */
	public boolean timeToShowAds() 
	{
		return (System.currentTimeMillis() - mSharedPreferences.getLong("ADS_LAST_SHOWN_TIME_MILLIS", 0L) ) > 7 * 60 * 60 * 1000;
	}
	
	public void setAdsShownNow()
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putLong("ADS_LAST_SHOWN_TIME_MILLIS", System.currentTimeMillis());
		e.commit();
	}
}
