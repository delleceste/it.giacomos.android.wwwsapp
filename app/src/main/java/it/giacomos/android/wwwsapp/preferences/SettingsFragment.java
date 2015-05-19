package it.giacomos.android.wwwsapp.preferences;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.service.ServiceManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment  implements OnPreferenceChangeListener 
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.getPreferenceManager().setSharedPreferencesName(Settings.PREFERENCES_NAME);
		this.addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		init(getActivity());
	}
	
	/** Initialize summaries according to the stored values */
	public void init(Activity a)
	{
		String svalue;
		findPreference("NOTIFICATION_SERVICE_ENABLED").setOnPreferenceChangeListener(this);
		findPreference("RAIN_NOTIFICATION_ENABLED").setOnPreferenceChangeListener(this);
		//findPreference("USE_INTERNAL_RAIN_DETECTION").setOnPreferenceChangeListener(this);

		/* initialize edit text fields */
		EditTextPreference tep = (EditTextPreference )findPreference("SERVICE_SLEEP_INTERVAL_MINS");
		svalue = tep.getSharedPreferences().getString("SERVICE_SLEEP_INTERVAL_MINS", "5");
		String s = getResources().getString(R.string.pref_service_sleep_interval_summary_checks_every);
		s += " " + svalue + " " + getResources().getString(R.string.minutes);
		tep.setSummary(s);
		tep.setOnPreferenceChangeListener(this);

		tep = (EditTextPreference) findPreference("MIN_TIME_BETWEEN_NOTIFICATIONS_RainNotificationTag");
		svalue = tep.getSharedPreferences().getString("MIN_TIME_BETWEEN_NOTIFICATIONS_RainNotificationTag", "30");
		s = getResources().getString(R.string.pref_rain_notif_interval);
		s += " " + svalue + " " + getResources().getString(R.string.minutes);
		tep.setSummary(s);
		tep.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) 
	{
		Log.e("onPreferenceChange", "key " + preference.getKey());
		boolean ret = true;
		int interval = -1;
		if(preference.getKey().equalsIgnoreCase("NOTIFICATION_SERVICE_ENABLED"))
		{
			boolean checked = newValue.equals(true);
			Log.e("onPreferenceChange", "starting service: " + checked);
			mStartNotificationService(checked);
		}
		else if(preference.getKey().equalsIgnoreCase("SERVICE_SLEEP_INTERVAL_MINS"))
		{
			interval = 5;
			try
			{
				interval = Integer.parseInt(newValue.toString());
				if(interval >= 1 && interval <= 15)
				{
					String s = getResources().getString(R.string.pref_service_sleep_interval_summary_checks_every);
					s += " " + newValue.toString() + " " + getResources().getString(R.string.minutes);
					preference.setSummary(s);
					/* restart service, if running */
					ServiceManager serviceManager = new ServiceManager();
					if(serviceManager.isServiceRunning(getActivity()))
					{
						serviceManager.setEnabled(getActivity(), false);
						serviceManager.setEnabled(getActivity(), true);
					}
				}
				else
					ret = false;
			}
			catch(NumberFormatException e)
			{
				ret = false;
			}
		}
		else if(preference.getKey().equalsIgnoreCase("MIN_TIME_BETWEEN_NOTIFICATIONS_RainNotificationTag"))
		{
			interval = 30;
			try
			{
				interval = Integer.parseInt(newValue.toString());
				if(interval >= 1 && interval <= 60 * 24)
				{
					String s = getResources().getString(R.string.pref_rain_alert_summary_checks_every);
					s += " " + newValue.toString() + " " + getResources().getString(R.string.minutes);
					preference.setSummary(s);
				}
				else
					ret = false;
			}
			catch(NumberFormatException e)
			{
				ret = false;
			}
		}
		
		if(interval > 0 && !ret) /* the user has edited an interval */
			Toast.makeText(getActivity(), R.string.notificationIntervalBetween0And180, Toast.LENGTH_LONG).show();
		
		return ret;
	}	

	private boolean mStartNotificationService(boolean startService) 
	{
		ServiceManager serviceManager = new ServiceManager();
		Log.e("SettingsActivity.mStartNotificationService", "enabling service: " + startService +
				" was running "+ serviceManager.isServiceRunning(getActivity()));

		boolean ret = serviceManager.setEnabled(getActivity(), startService);
		if(ret && startService)
			Toast.makeText(getActivity(), R.string.notificationServiceStarted, Toast.LENGTH_LONG).show();
		else if(ret && !startService)
			Toast.makeText(getActivity(), R.string.notificationServiceStopped, Toast.LENGTH_LONG).show();
		else if(!ret && startService)
			Toast.makeText(getActivity(), R.string.notificationServiceWillStartOnNetworkAvailable, Toast.LENGTH_LONG).show();

		return (startService && ret || !startService);
	}

}
