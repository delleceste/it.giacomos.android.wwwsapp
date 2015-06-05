package it.giacomos.android.wwwsapp.report.widgets;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.report.WidgetValue;

/**
 * Created by giacomo on 5/06/15.
 */
public class DataValuesSpinnerAdapter extends ArrayAdapter<WidgetValue>
{

    private Activity mActivity;
    public DataValuesSpinnerAdapter(Context context, int resource,
                                    ArrayList<WidgetValue> data, Activity activity)
    {
        super(context, resource);
        mActivity = activity;
        this.addAll(data);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View row = inflater.inflate(R.layout.post_icon_text_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.text);
        if (position < this.getCount())
        {
            WidgetValue v = this.getItem(position);
            label.setText(v.text);
            if (v.icon != null)
            {
                ImageView icon = (ImageView) row.findViewById(R.id.icon);
                icon.setImageBitmap(v.icon);
            }
        }
        return row;
    }
}

