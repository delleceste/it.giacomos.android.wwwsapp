package it.giacomos.android.wwwsapp.network;

public interface NetworkStatusMonitorListener {
	void onNetworkBecomesAvailable();
	void onNetworkBecomesUnavailable();

}
