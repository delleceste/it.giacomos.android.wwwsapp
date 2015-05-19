package it.giacomos.android.wwwsapp.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.locationUtils.GeoCoordinates;
import it.giacomos.android.wwwsapp.preferences.Settings;
import it.giacomos.android.wwwsapp.widgets.OAnimatedTextView;
import it.giacomos.android.wwwsapp.widgets.map.report.OnTiltChangeListener;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostReportAsyncTaskPool;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.location.Location;
import android.os.Bundle;

public class OMapFragment extends SupportMapFragment implements
GoogleMap.OnCameraChangeListener,
OnMapReadyCallback
{
	private float mOldZoomLevel;
	private boolean mCenterOnUpdate;
	private boolean mMapReady; /* a map is considered ready after first camera update */
	private MapViewMode mMode = null;
	private GoogleMap mMap;
	private CameraPosition mSavedCameraPosition;
	private ZoomChangeListener mZoomChangeListener;
	private OnTiltChangeListener mOnTiltChangeListener;
	private ArrayList <OOverlayInterface> mOverlays;
	private Settings mSettings;

	/* MapFragmentListener: the activity must implement this in order to be notified when 
	 * the GoogleMap is ready.
	 */
	private MapFragmentListener mMapFragmentListener;

	public OMapFragment() 
	{
		super();
		mCenterOnUpdate = false;
		mMapReady = false;
		mOldZoomLevel = -1.0f;
		mZoomChangeListener = null;
		mSavedCameraPosition = null;
		mMapFragmentListener = null;
		mOverlays = new ArrayList<OOverlayInterface>();
		mMode = new MapViewMode();
		mMode.isInit = true;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) 
	{
		
	}
	
	@Override
	public void onCameraChange(CameraPosition cameraPosition) 
	{
		/* mCenterOnUpdate is true if mSettings.getCameraPosition() returns null.
		 * This happens when the application is launched for the first time.
		 */
		if(mCenterOnUpdate)
		{
			/* center just once */
			mCenterOnUpdate = false;
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(GeoCoordinates.regionBounds, 20);
			mMap.animateCamera(cu);
		}

		if(getActivity() != null && getActivity().findViewById(R.id.mapMessageTextView) != null)
		{
			OAnimatedTextView radarUpdateTimestampText = (OAnimatedTextView) getActivity().findViewById(R.id.mapMessageTextView);
			if(mMapReady && radarUpdateTimestampText.getVisibility() == View.VISIBLE && !radarUpdateTimestampText.animationHasStarted())
				radarUpdateTimestampText.animateHide();	
		}

		if(mSavedCameraPosition != null)
		{
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mSavedCameraPosition));
			mSavedCameraPosition = null; /* restore camera just once! */
		}
		else
		{
			if(mOldZoomLevel != cameraPosition.zoom && mZoomChangeListener != null)
				mZoomChangeListener.onZoomLevelChanged(cameraPosition.zoom);
			mOldZoomLevel = cameraPosition.zoom;
		}

		if(!mMapReady)
		{
			mMapReady = true;
			mMapFragmentListener.onCameraReady();
		}
		
		if(mOnTiltChangeListener != null)
			mOnTiltChangeListener.onTiltChanged(cameraPosition.tilt);
	} 

	public void onStart()
	{
		super.onStart();
	}

	public void onDestroy ()
	{
		mRemoveOverlays();
		super.onDestroy();
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		/* get the GoogleMap object. Must be called after onCreateView is called.
		 * If it returns null, then Google Play services is not available.
		 */
		getMapAsync(this);
	}
	
	public void onResume()
	{
		super.onResume();
		mMap.setMyLocationEnabled(true);
	}
	
	public void onPause()
	{
		super.onPause();
		
		/* mMapReady is true if onCameraChanged has been called at least one time.
		 * This ensures that the map camera has been initialized and is not centered
		 * in lat/lang (0.0, 0.0). If mMapReady is true we correctly save an initialized
		 * camera position.
		 */
		if(mMapReady)
		{
			mSettings.saveMapCameraPosition(mMap.getCameraPosition());
			/* save the map type */
			mSettings.setMapType(mMap.getMapType());
		}
		
		if(mMap != null)
			mMap.setMyLocationEnabled(false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState); /* modificato x map v2 */
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mMap = getMap();
		UiSettings uiS = mMap.getUiSettings();
		//	uiS.setRotateGesturesEnabled(false);
		uiS.setZoomControlsEnabled(false);
		
		HelloWorldActivity oActivity = (HelloWorldActivity) getActivity();
		mSettings = new Settings(oActivity.getApplicationContext());
		/* restore last used map type */
		mMap.setMapType(mSettings.getMapType());
		/* restore last camera position */
		mSavedCameraPosition = mSettings.getCameraPosition();
		if(mSavedCameraPosition == null) /* never saved */
			mCenterOnUpdate = true;
		mMap.setOnCameraChangeListener(this);

		/* set html text on Radar info text view */
		TextView radarInfoTextView = (TextView) getActivity().findViewById(R.id.radarInfoTextView);
		radarInfoTextView.setText(Html.fromHtml(getResources().getString(R.string.radar_info)));
		radarInfoTextView.setVisibility(View.GONE);
		
		/* when the activity creates us, it passes the initialization stuff through arguments */
		mSetMode(mMode);
		oActivity.findViewById(R.id.mapMessageTextView).setVisibility(View.GONE);

	}

	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void centerMap() 
	{
		if(mMapReady)
		{
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(GeoCoordinates.regionBounds, 20);
			mMap.animateCamera(cu);
		}
	}

	public void moveTo(double latitude, double longitude)
	{
		if(mMapReady)
		{
			CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
			mMap.moveCamera(cu);
		}
	}

	public void setMode(MapViewMode m)
	{
		if(mMap != null)
			mSetMode(m);
		else
		{
			mMode = m;
			mMode.isInit = true;
		}
	}
	
	private void mSetMode(MapViewMode m)
	{

	}

	public void setMapFragmentListener(MapFragmentListener mfl)
	{
		mMapFragmentListener = mfl;
	}
	
	public void setOnZoomChangeListener(ZoomChangeListener l)
	{
		mZoomChangeListener = l;
	}

	public MapViewMode getMode()
	{
		return mMode;
	}

	public void setTerrainEnabled(boolean satEnabled)
	{
		mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	}

	public boolean isTerrainEnabled()
	{
		return mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN;
	}

	public void setSatEnabled(boolean satEnabled)
	{
		mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	}

	public boolean isSatEnabled()
	{
		return mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE;
	}

	public void setNormalViewEnabled(boolean checked) 
	{
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}

	public boolean isNormalViewEnabled()
	{
		return mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL;
	}

	public boolean isInfoWindowVisible()
	{
		for(int i = 0; i < mOverlays.size(); i++)
			if(mOverlays.get(i).isInfoWindowVisible())
				return true;
		return false;
	}

	public void hideInfoWindow()
	{
		for(int i = 0; i < mOverlays.size(); i++)
			mOverlays.get(i).hideInfoWindow();
	}
	
	private void mRemoveOverlays()
	{
		for(int i = 0; i < mOverlays.size(); i++)
			mOverlays.get(i).clear();
		mOverlays.clear();
	}

	private void mUninstallAdaptersAndListeners()
	{
		if(mMap != null)
		{
			mMap.setInfoWindowAdapter(null);
			mMap.setOnMapClickListener(null);
			mMap.setOnMarkerClickListener(null);
			mMap.setOnMarkerDragListener(null);
			mMap.setOnInfoWindowClickListener(null);
			mMap.setOnMapLongClickListener(null);
			mMap.setOnMarkerDragListener(null);
		}
		setOnZoomChangeListener(null);
		mOnTiltChangeListener = null;
	}
	
	public boolean pointTooCloseToMyLocation(Location myLocation, LatLng pointOnMap)
	{
		Location pt = new Location("");
		pt.setLatitude(pointOnMap.latitude);
		pt.setLongitude(pointOnMap.longitude);
		return myLocation.distanceTo(pt) < 500;
	}

}
