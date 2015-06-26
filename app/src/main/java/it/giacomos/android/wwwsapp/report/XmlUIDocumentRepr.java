package it.giacomos.android.wwwsapp.report;

import java.util.HashMap;

/**
 * Created by giacomo on 17/06/15.
 */
public class XmlUIDocumentRepr
{
    private String mLayerName, mTitle, mError;

    private HashMap<String, XmlUiProperty> mProperties;

    public XmlUIDocumentRepr(String layerName)
    {
        mLayerName = layerName;
        mProperties = new HashMap<String, XmlUiProperty>();
        mError = "";
    }

    public String getLayerName()
    {
        return mLayerName;
    }

    public String getTitle()
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
    }

    public boolean hasProperty(String propertyName)
    {
        return mProperties.containsKey(propertyName);
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
