package it.giacomos.android.wwwsapp.report;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.FileUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportData extends DataInterface
{
	public String  writable;
	private Marker mMarker;
	private MarkerOptions mMarkerOptions;
	private long  mEventId;
	
	public ReportData(int eventId, String layNam, double lat, double lon, String datet, String userDisplayNam)
	{
		super(layNam, lat, lon, datet, userDisplayNam);
		mMarker = null;
		writable = "r";
		mEventId = eventId;
	}

    public boolean sameAs(DataInterface other)
    {
		if(other.getType() == DataInterface.TYPE_REPORT)
			return ((ReportData) other).getEventId() == mEventId;
		return false;
    }

	public boolean isWritable()
	{
		return (writable.compareTo("w") == 0);
	}

	@Override
	public  String getId()
	{
		/* event id is unique across all layers */
		return String.valueOf(DataInterface.TYPE_REPORT) + ":eventId:" + String.valueOf(mEventId);
	}

	@Override
	public int getType() 
	{
		return DataInterface.TYPE_REPORT;
	}

	@Override
	public MarkerOptions buildMarkerOptions(Context ctx, XmlUIDocumentRepr dataRepr)
	{
		String title = "";
		String snippet = "";
        String locality;
		mMarkerOptions = null;

		title = getUserDisplayName();
		snippet = getLayerName() + ", " + getDateTime() + "\n";

		locality = getLocality();
		if(locality.length() > 1) /* different from "-" */
			title += " - " + locality;
		
		snippet += "\n" + getDataRepr();
		
		mMarkerOptions = new MarkerOptions();
		mMarkerOptions.position(new LatLng(getLatitude(), getLongitude()));
		mMarkerOptions.title(title);
		mMarkerOptions.snippet(snippet);

        BitmapDescriptor bitmapDescriptor = null;
		if(dataRepr.hasMarkerIcon())
		{
			FileUtils fu = new FileUtils();
			Bitmap markerIcon = fu.loadBitmapFromStorage("layers/" + getLayerName() + "/bmps/" + dataRepr.getMarkerIcon() + ".bmp", ctx);
			/* for sky no label, so do not use obsBmpFactory */
			bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerIcon);
			mMarkerOptions.icon(bitmapDescriptor);
			mMarkerOptions.anchor(0.5f, 0.5f);
		}
		else
			mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

		return mMarkerOptions;
	}

	public void setMarker(Marker m)
	{
		mMarker = m;
	}
	
	public MarkerOptions getMarkerOptions()
	{
		return mMarkerOptions;
	}
	
	@Override
	public Marker getMarker() 
	{
		return mMarker;
	}

	/** @return true: always return true for reports.
	 * 
	 */
	@Override
	public boolean isPublished() {
		return true;
	}


	public long getEventId()
	{
		return mEventId;
	}
	
}
