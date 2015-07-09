package it.giacomos.android.wwwsapp.report;

import it.giacomos.android.wwwsapp.R;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RequestData extends DataInterface
{
	private boolean writable;
	public String display_name, locality;
	private Marker mMarker;
	private MarkerOptions mMarkerOptions;
	private boolean mIsPublished;

	/** Builds a RequestData object
	 * 
	 * @param datetime the date time in string format
	 * @param user the user name
	 * @param local the locality associated to the request
	 * @param la the latitude
	 * @param lo the longitude
	 * @param wri writable attribute: if true: the user owns the request.
	 * @param isPublished if true: the request has been published on the database
	 */
	public RequestData(String datetime, String user, String local,
			double la, double lo, boolean wri, boolean isPublished)
	{
		super("RequestData", la, lo, datetime, user);
		writable = wri;
		display_name = user;
		locality = local;
		mIsPublished = isPublished;
	}

	public boolean isWritable()
	{
		return writable;
	}

	@Override
	public int getType() {
		return DataInterface.TYPE_REQUEST;
	}

	@Override
	public String getLocality() {
		return locality;
	}
	
	public void setLocality(String loc)
	{
		locality = loc;
	}

	@Override
	public MarkerOptions buildMarkerOptions(Context ctx, XmlUIDocumentRepr repr)
	{
		Resources res = ctx.getResources();
		String  title;

		mMarkerOptions = new MarkerOptions();
		mMarkerOptions.position(new LatLng(getLatitude(), getLongitude()));
		if(!isWritable())
		{
			title = res.getString(R.string.reportRequested);
			mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		}
		else
		{
			if(!mIsPublished)
			{
				title = res.getString(R.string.reportRequest);
				mMarkerOptions.draggable(true);
				mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			}
			else
			{
				title = res.getString(R.string.reportRequestPublished);
				mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			}
		}

		mMarkerOptions.title(title);
		mMarkerOptions.snippet(mMakeSnippet(locality, ctx));

		return mMarkerOptions;
	}

	public void setMarker(Marker m)
	{
		mMarker = m;
	}

	@Override
	public Marker getMarker() {
		return mMarker;
	}

	@Override
	public String getId()
	{
		return String.valueOf(TYPE_REQUEST) + ":" + getLayerName() + ":LatLon" + String.valueOf(getLatitude()) + ":" + String.valueOf(getLongitude()) + ":" + display_name;
	}

	@Override
	public MarkerOptions getMarkerOptions() {
		return mMarkerOptions;
	}

	public boolean isPublished()
	{
		return mIsPublished;
	}
	
	public void setPublished(boolean published)
	{
		mIsPublished = published;
	}

	public void updateWithLocality(String locality, Context applicationContext) 
	{
		if(mMarker != null)
		{
			mMarker.setSnippet(mMakeSnippet(locality, applicationContext));
		}
	}

	private String mMakeSnippet(String locality, Context ctx)
	{
		Resources res = ctx.getResources();
		String snippet = display_name;
		if(locality.length() > 0)
			snippet += "\n" + res.getString(R.string.reportLocality) + ": " + locality;
		//snippet += "\nlat. " + latitude + ", long. " + longitude;

		if(isWritable())
		{
			if(!mIsPublished)
				snippet += "\n" + res.getString(R.string.reportTouchBaloonToRequest);
			else
			{
				snippet += "\n" + res.getString(R.string.reportRequestPublishedOn) + " " + getDateTime();
				snippet += "\n" + res.getString(R.string.reportTouchBaloonToCancel);
			}
		}
		else
		{
			snippet += "\n" + res.getString(R.string.reportRequestPublishedOn) + " " + getDateTime();
			snippet += "\n* " + res.getString(R.string.reportTouchBaloonToReport) + " " + display_name;
 		}
		return snippet;
	}
}
