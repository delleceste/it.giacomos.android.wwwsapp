package it.giacomos.android.wwwsapp.report;

import android.graphics.Bitmap;

/**
 * Created by giacomo on 5/06/15.
 */
public class WidgetValue
{
    public String text;
    public String value;
    public Bitmap icon;
    public boolean isValid;

    public WidgetValue(String txt, String val)
    {
        text = txt;
        isValid = true;
        icon = null;
        value = val;
    }
}
