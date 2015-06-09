package it.giacomos.android.wwwsapp.report;

import android.util.Log;
import android.view.View;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import it.giacomos.android.wwwsapp.report.widgets.TextValueInterface;

/**
 * Created by giacomo on 5/06/15.
 */
public class ReportDataBuilder
{
    private String mString;
    private HashMap<String, String> mData;

    public void build(ArrayList<WidgetData> data, ReportActivity activity)
    {
        String value, param;
        mData = new HashMap<String, String>();
        for(WidgetData d : data)
        {
            int id = d.id;
            View v = activity.findViewById(id);
            if(v != null)
            {
                try
                {
                    TextValueInterface tvi = (TextValueInterface) v;
                    value = tvi.getValueAsText();
                    param = d.name;
                    mData.put(param, value);
                }
                catch(ClassCastException e)
                {
                    Log.e("ReportDataBuilder.build", "Cast error: " + e.getLocalizedMessage());
                }
            }
        }
    }

    public void add(String key, double value)
    {
        mData.put(key, String.valueOf(value));
    }

    public void add(String key, String value)
    {
        mData.put(key, value);
    }

    public String toString()
    {
        String s = "";
        if(mData != null)
        {
            for (String k : mData.keySet())
                try
                {
                    if(!s.isEmpty())
                        s += "&";
                    s += URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(mData.get(k), "UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
        }

        return s;
    }
}
