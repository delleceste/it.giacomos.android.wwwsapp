package it.giacomos.android.wwwsapp.locationUtils;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class LocationService implements LocationListener,
GeocodeAddressUpdateListener, ConnectionCallbacks, OnConnectionFailedListener
{
	private final Context mContext;
	private GoogleApiClient mGoogleApiClient;
	private ArrayList<LocationServiceUpdateListener> mLocationServiceUpdateListeners;
	private ArrayList<LocationServiceAddressUpdateListener> mLocationServiceAddressUpdateListeners;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;
	private LocationInfo mCurrentLocationInfo;
		
	/* store location services available flag if servicesAvailable returns true */
	
	/* Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	public final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	public LocationService(Context ctx)
	{
		mContext = ctx;
		mGoogleApiClient = null;
		mCurrentLocation = null;
		mCurrentLocationInfo = null;
		mLocationServiceUpdateListeners = new ArrayList<LocationServiceUpdateListener>();
		mLocationServiceAddressUpdateListeners = new ArrayList<LocationServiceAddressUpdateListener>();
	}
	
	public Location getCurrentLocation()
	{
		return mCurrentLocation;
	}
	
	public LocationInfo getCurrentLocationInfo()
	{
		return mCurrentLocationInfo;
	}
	
	public boolean isConnected()
	{
		return mGoogleApiClient != null && mGoogleApiClient.isConnected();
	}
	
	/* to be called onStart()
	 *
	 */
	public boolean connect()
	{
		/* servicesAvailable has an empty implementation.
		 * GooglePlay services check is done in HelloWorldActivity.
		 */
		boolean result = true;
//		result = servicesAvailable();
		
		/* Constructor:
		 * LocationClient(Context context, GooglePlayServicesClient.ConnectionCallbacks, 
		 * GooglePlayServicesClient.OnConnectionFailedListener) 
		 * 
		 */
		if(result)
		{
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setInterval(Constants.LOCATION_UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(Constants.LOCATION_FASTEST_UPDATE_INTERVAL);
			/* smallestDisplacementMeters the smallest displacement 
			 * in meters the user must move between location updates
			 */
			mLocationRequest.setSmallestDisplacement(100.0f);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API).
					addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
			mGoogleApiClient.connect();
		}
		return result;
	}
	
	/* to be called onStop()
	 * 
	 */
	public void disconnect()
	{
		if(mGoogleApiClient != null)
		{
			if(mGoogleApiClient.isConnected())
			{
				Log.e("LocationService.disconnect()", "removing location updates");
				mGoogleApiClient.unregisterConnectionCallbacks(this);
				mGoogleApiClient.unregisterConnectionFailedListener(this);
			}
			Log.e("LocationService.disconnect()", "disconnecting location client");
			mGoogleApiClient.disconnect();
		}
	}

	public boolean servicesAvailable() 
	{
		/* checks have been made inside Activity */
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) 
	{
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) 
        {
        	
        } 
        else 
        {
        	Log.e("LocationService.onConnectionFailed", "no resolution available");
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }
    }

	
	public void registerLocationServiceUpdateListener(LocationServiceUpdateListener l)
	{
		mLocationServiceUpdateListeners.add(l);
		if(mCurrentLocation != null) /* immediately notify listener if possible */
			l.onLocationChanged(mCurrentLocation);
	}
	
	public void removeLocationServiceUpdateListener(LocationServiceUpdateListener l)
	{
		mLocationServiceUpdateListeners.remove(l);
	}
	
	public void registerLocationServiceAddressUpdateListener(LocationServiceAddressUpdateListener al)
	{
		mLocationServiceAddressUpdateListeners.add(al);
		if(mCurrentLocationInfo != null)
		{
			/* immediately notify the just registered listener */
			al.onLocalityChanged(mCurrentLocationInfo.locality, 
					mCurrentLocationInfo.subLocality, 
					mCurrentLocationInfo.address);
		}
	}
	
	public void removeLocationServiceAddressUpdateListener(LocationServiceAddressUpdateListener al)
	{
		mLocationServiceAddressUpdateListeners.remove(al);
	}
	
	@Override
	public void onConnected(Bundle bundle) 
	{
		if(mGoogleApiClient != null)
		{
			Log.e("onConnected", "connected to loc cli");
			
			Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			if(lastKnownLocation != null)
			{
				mCurrentLocation = lastKnownLocation;
				onLocationChanged(lastKnownLocation);
			}
		}
	}

	public void updateGeocodeAddress()
	{
		GeocodeAddressTask geocodeAddressTask = new GeocodeAddressTask(mContext, this, "LocationService");
		geocodeAddressTask.parallelExecute(mCurrentLocation);
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		for(LocationServiceUpdateListener l : mLocationServiceUpdateListeners)
			l.onLocationChanged(location);
		mCurrentLocation = location;
		
		/* do we still need LocationComparer ? 
		 * ... hope not
		 */
		
		/* 
		 * LocationComparer locationComparer = new LocationComparer();
		 
		
		if(locationComparer.isBetterLocation(location, mCurrentLocation))
		{	
			mCurrentLocation = location; // save current location
			if(mDownloadStatus.isOnline)
			{
//				Log.e("LocationService.onLocationChanged", "we are online, starting geocode task");
				updateGeocodeAddress();
			}
		}
//		else
//			Log.e("LocationService.onLocationChanged", " !!!! new location is not better than old");
		locationComparer = null;
		*
		*/
	}

	@Override
	/* executed when a new locality / address becomes available.
	 */
	public void onGeocodeAddressUpdate(LocationInfo locInfo, String id_unused_here)
	{
		if(locInfo.error.isEmpty())
		{
			for(LocationServiceAddressUpdateListener lsal : mLocationServiceAddressUpdateListeners)
			{
				lsal.onLocalityChanged(locInfo.locality, locInfo.subLocality, locInfo.address);
			}
			mCurrentLocationInfo = locInfo;
		}
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
}

