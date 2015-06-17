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
	
	public ReportData(String layNam, double lat, double lon, String datet, String userDisplayNam)
	{
		super(layNam, lat, lon, datet, userDisplayNam);
		mMarker = null;
	}

    public boolean sameAs(DataInterface other)
    {
        if(this.getType() != other.getType())
            return false;
        ReportData o = (ReportData) other;
        return writable.compareTo(o.writable) == 0 && getLatitude() == o.getLatitude() &&
                getLongitude() == o.getLongitude() &&
                getDataRepr().compareTo(o.getDataRepr()) == 0 &&
                getUserDisplayName().compareTo(o.getUserDisplayName()) == 0 &&
                getLayerName().compareTo(o.getLayerName()) == 0 &&
                getLocality().compareTo(o.getLocality() ) == 0;
    }

	public boolean isWritable()
	{
		return (writable.compareTo("w") == 0);
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

	
	
}
