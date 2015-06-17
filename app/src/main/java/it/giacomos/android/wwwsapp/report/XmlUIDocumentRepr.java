package it.giacomos.android.wwwsapp.report;

import java.util.HashMap;

/**
 * Created by giacomo on 17/06/15.
 */
public class XmlUIDocumentRepr
{
    private String mLayerName, mTitle, mError;

    private String mMarkerIcon;

    private HashMap<String, XmlUiProperty> mProperties;

    public XmlUIDocumentRepr(String layerName)
    {
        mLayerName = layerName;
        mProperties = new HashMap<String, XmlUiProperty>();
        mError = "";
        mMarkerIcon = "";
    }

    public String getLayerName()
    {
        return mLayerName;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public void setTitle(String ti)
    {
        mTitle = ti;
    }

    public void setError(String err)
    {
        mError = err;
    }

    public String getError()
    {
        return mError;
    }

    public boolean hasError()
    {
        return !mError.isEmpty();
    }

    public void addProperty(String name, XmlUiProperty prop)
    {
        mProperties.put(name, prop);
        if(prop.isMarkerIcon())
            mMarkerIcon = prop.getMarkerIcon();
    }

    public  String getMarkerIcon()
    {
        return mMarkerIcon;
    }

    public boolean hasMarkerIcon()
    {
        return mMarkerIcon.length() > 0;
    }

    public XmlUiProperty get(String propertyName)
    {
        if(mProperties.containsKey(propertyName) )
        {
            return mProperties.get(propertyName);
        }
        return null;
    }
}
