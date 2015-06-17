package it.giacomos.android.wwwsapp.report;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by giacomo on 17/06/15.
 */
public class XmlUiProperty
{
    private HashMap<String, Bundle> mDataHash;
    private String mName, mType, mText, mMarkerIcon;
    private boolean mIsMarkerIcon;

    public XmlUiProperty(String name, String text, String type)
    {
        mDataHash = new HashMap<String, Bundle> ();
        mName = name;
        mText = text;
        mType = type;
    }

    public void setIsMarkerIcon(boolean is)
    {
        mIsMarkerIcon = is;
    }

    public boolean isMarkerIcon()
    {
        return mIsMarkerIcon;
    }

    public String getMarkerIcon()
    {
        return mMarkerIcon;
    }

    public String getName()
    {
        return mName;
    }

    public void addValue(String value, String text, String icon)
    {
        Bundle b = new Bundle();
        b.putString("value", value);
        b.putString("text", text);
        b.putString("icon", icon);

        mDataHash.put(value, b);
    }
}
