package it.giacomos.android.wwwsapp.locationUtils;

import android.location.Location;
import android.util.Log;

/* taken from 
 * http://developer.android.com/guide/topics/location/strategies.html
 */
public class LocationComparer {
	
	private static final int INTERVAL = Constants.LOCATION_COMPARER_INTERVAL; /* 60 seconds from 1.1.1 */

	/**
	 * 
	 * @param newLocation the new location received from the provider
	 * @param currentBestLocation the current location, previously used
	 * @return true if the new location is better than the previous, false otherwise
	 */
	public boolean isBetterLocation(Location newLocation, Location currentBestLocation) {
	    if (currentBestLocation == null) {
//	    	Log.e("isBetterLocation", "current best location is NULL, return true");
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > INTERVAL;
	    boolean isSignificantlyOlder = timeDelta < -INTERVAL;
	    boolean isNewer = timeDelta > 0;
	    
	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
	            currentBestLocation.getProvider());

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > Constants.LOCATION_COMPARER_ACCURACY;

	    /* if the location comes from GPS, is newer and is not significantly less accurate, then
	     * return true. Assume that GPS location is always worth to be used to update the position.
	     */
	    if (isNewer && !isSignificantlyLessAccurate && newLocation.getProvider().equals("gps")) 
	    {
//	    	Log.e("isBetterLocation", "true, 1");
	        return true;
	    }
	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) 
	    {
//	    	Log.e("isBetterLocation", "true, isSignificantlyNewer");
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } 
	    else if (isSignificantlyOlder) 
	    {
//	    	Log.e("isBetterLocation", "false, isSignificantlyOlder");
	        return false;
	    }

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) 
	    {
//	    	Log.e("isBetterLocation", "true, isMoreAccurate");
	        return true;
	    } 
	    else if (isNewer && !isLessAccurate) 
	    {
//	    	Log.e("isBetterLocation", "true, isNewer && !isLessAccurate");
	        return true;
	    } 
	    else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) 
	    {
//	    	Log.e("isBetterLocation", "isNewer && !isSignificantlyLessAccurate && isFromSameProvider");
	        return true;
	    }
//	    Log.e("isBetterLocation", "false");
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) 
	{
	    if (provider1 == null) 
	    {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

}
