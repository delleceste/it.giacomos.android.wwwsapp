package it.giacomos.android.wwwsapp.report;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.layers.FileUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class ReportData extends DataInterface
{
    public String writable;
    private Marker mMarker;
    private MarkerOptions mMarkerOptions;
    private long mEventId;

    public ReportData(int eventId, String layNam, double lat, double lon, String datet, String userDisplayNam)
    {
        super(layNam, lat, lon, datet, userDisplayNam);
        mMarker = null;
        writable = "r";
        mEventId = eventId;
    }

    public boolean sameAs(DataInterface other)
    {
        if (other.getType() == DataInterface.TYPE_REPORT)
            return ((ReportData) other).getEventId() == mEventId;
        return false;
    }

    public boolean isWritable()
    {
        return (writable.compareTo("w") == 0);
    }

    @Override
    public String getId()
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
        String value;
        String s, iconName;
        mMarkerOptions = null;
        BitmapDescriptor bitmapDescriptor = null;

        title = getUserDisplayName() + " - " + getLayerName();
        snippet = getLayerName() + ", " + getDateTime() + "\n";

        locality = getLocality();
        if (locality.length() > 1) /* different from "-" */
            title += " - " + locality;

        mMarkerOptions = new MarkerOptions();
        HashMap<String, String> data = getData();
        for (String k : data.keySet())
        {
            value = data.get(k);
            if (dataRepr.hasProperty(k) && !value.isEmpty())
            {
                XmlUiProperty p = dataRepr.get(k);
                s = p.getValueText(value);
                if (s.isEmpty()) /* no predefined text for a given value, such as a temperature for example */
                    s = value;
                snippet += "\n" + k + ": " + s;
                if (p.isMarkerIcon() && bitmapDescriptor == null) /* test if null to generate the icon once */
                {
                    iconName = p.getValueIcon(value);
                    FileUtils fu = new FileUtils();
                    Log.e("ReportData.buildMO", "has marker icon: " + "layers/" + getLayerName() + "/bmps/" + iconName+ ".bmp");
                    Bitmap markerIcon = fu.loadBitmapFromStorage("layers/" + getLayerName() + "/bmps/" + iconName + ".bmp", ctx);
                    if(markerIcon != null)
                    {
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerIcon);
                        mMarkerOptions.icon(bitmapDescriptor);
                        Log.e("ReportData.buildMO", "set icon in marker options from " + markerIcon + " bitmaPDesc " + bitmapDescriptor);
                        mMarkerOptions.anchor(0.5f, 0.5f);
                    }
                }
            }
        }

        if(bitmapDescriptor == null)
            mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        // snippet += getDataRepr();

        mMarkerOptions.position(new LatLng(getLatitude(), getLongitude()));
        mMarkerOptions.title(title);
        mMarkerOptions.snippet(snippet);

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

    /**
     * @return true: always return true for reports.
     */
    @Override
    public boolean isPublished()
    {
        return true;
    }


    public long getEventId()
    {
        return mEventId;
    }

}
