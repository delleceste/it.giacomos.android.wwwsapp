package it.giacomos.android.wwwsapp.report;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by giacomo on 5/06/15.
 */
public class ReportDataBuilder
{
    public HashMap<String, String> build(ArrayList<WidgetData> data, ReportActivity activity)
    {
        HashMap<String, String> m = new HashMap<String, String>();
        for(WidgetData d : data)
        {
            int id = d.id;
            View v = activity.findViewById(id);

        }

        return m;
    }
}
