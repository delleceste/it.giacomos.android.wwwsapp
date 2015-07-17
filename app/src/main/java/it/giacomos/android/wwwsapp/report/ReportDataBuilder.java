package it.giacomos.android.wwwsapp.report;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import it.giacomos.android.wwwsapp.report.widgets.TextValueInterface;
import it.giacomos.android.wwwsapp.service.PostDataServiceParamsBuilder;

/**
 * Created by giacomo on 5/06/15.
 */
public class ReportDataBuilder extends PostDataServiceParamsBuilder
{
    public void build(ArrayList<WidgetData> data, Activity activity)
    {
        String value, param;
        for(WidgetData d : data)
        {
            int id = d.id;
            View v = activity.findViewById(id);
            if(v != null)
            {
                try
                {
                    TextValueInterface tvi = (TextValueInterface) v;
                    value = tvi.getValue();
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

}
