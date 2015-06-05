package it.giacomos.android.wwwsapp.report;

import java.util.ArrayList;

/**
 * Created by giacomo on 5/06/15.
 */
public class WidgetData
{
    public ArrayList<WidgetValue> values;

    public String name, type, text, representation;
    public boolean isOption, isCategory;
    int id;

    public WidgetData(int wid, String nam, String ty, String txt, String repr)
    {
        id = wid;
        name = nam;
        type = ty;
        representation = repr;
        values = new ArrayList<WidgetValue>();
        isOption = false;
        isCategory = false;
    }

    public void addValue(WidgetValue v)
    {
        if(values == null)
            values = new ArrayList<WidgetValue>();
        values.add(v);
    }
}
