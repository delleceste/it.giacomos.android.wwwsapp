package it.giacomos.android.wwwsapp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStatusMonitor extends BroadcastReceiver 
{
	public NetworkStatusMonitor(NetworkStatusMonitorListener networkStatusMonitorListener, Context ctx)
	{
		m_networkStatusMonitorListener = networkStatusMonitorListener;
		mUpdate(ctx);
	}

	private void mUpdate(Context context)
	{
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo currentNetworkInfo = cm.getActiveNetworkInfo();
		m_isConnected = currentNetworkInfo != null && currentNetworkInfo.isConnected();
	}

	public  void onReceive(Context context, Intent intent)
	{
		mUpdate(context);
		Log.e("NetworkStatusMon.onReceive", "is connected " + m_isConnected);
		if(m_isConnected)
			m_networkStatusMonitorListener.onNetworkBecomesAvailable();
		else
			m_networkStatusMonitorListener.onNetworkBecomesUnavailable();
	}

	public boolean isConnected()
	{
		return m_isConnected;
	}
	
	private NetworkStatusMonitorListener m_networkStatusMonitorListener;
	
	private boolean m_isConnected;

}
