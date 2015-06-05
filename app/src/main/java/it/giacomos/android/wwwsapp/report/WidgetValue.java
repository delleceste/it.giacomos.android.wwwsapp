package it.giacomos.android.wwwsapp.report;

import android.graphics.Bitmap;

/**
 * Created by giacomo on 5/06/15.
 */
public class WidgetValue
{
    public String text;
    public Bitmap icon;
    public boolean isValid;

    public WidgetValue(String txt)
    {
        text = txt;
        isValid = true;
        icon = null;
    }
}
