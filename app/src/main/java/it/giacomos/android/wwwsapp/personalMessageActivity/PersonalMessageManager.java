package it.giacomos.android.wwwsapp.personalMessageActivity;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.service.ServiceManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PersonalMessageManager 
{
	public PersonalMessageManager(HelloWorldActivity helloWorldActivity, PersonalMessageData data) 
	{
		/* stop service */
		ServiceManager serviceManager = new ServiceManager();
//		Log.e("PersonalMessageManager", "created: is service running: " 
//				+ serviceManager.isServiceRunning(osmerActivity));
		boolean ret = serviceManager.setEnabled(helloWorldActivity, false);
		if(ret)
			Toast.makeText(helloWorldActivity, R.string.service_stopped_app_blocked, Toast.LENGTH_LONG).show();
		
		Intent i = new Intent(helloWorldActivity, PersonalMessageActivity.class);
		i.putExtra("name", data.title);
		i.putExtra("message", data.message);
		i.putExtra("date", data.date);
		helloWorldActivity.startActivity(i);
		
		if(data.blocking)
			helloWorldActivity.finish();
	}
}
