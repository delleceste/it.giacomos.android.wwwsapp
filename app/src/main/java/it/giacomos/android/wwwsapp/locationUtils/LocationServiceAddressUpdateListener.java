package it.giacomos.android.wwwsapp.locationUtils;

public interface LocationServiceAddressUpdateListener 
{
	public abstract void onLocalityChanged(String locality, String subLocality, String address);
}
