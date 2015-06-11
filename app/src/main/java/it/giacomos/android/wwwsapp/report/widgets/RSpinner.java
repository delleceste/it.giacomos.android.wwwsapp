package it.giacomos.android.wwwsapp.report.widgets;

import android.content.Context;
import android.graphics.Color;
import android.widget.Spinner;
import android.widget.TextView;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.report.WidgetValue;

/**
 * Created by giacomo on 5/06/15.
 */
public class RSpinner extends Spinner implements TextValueInterface
{

    public RSpinner(Context context)
    {
        super(context);
    }

    @Override
    public String getValue()
    {
        String s = "";
        DataValuesSpinnerAdapter spinnerAdapter = (DataValuesSpinnerAdapter) getAdapter();
        if(spinnerAdapter != null)
        {
            WidgetValue v = spinnerAdapter.getItem(getSelectedItemPosition());
            s = v.value;
        }
        return  s;
    }

    @Override
    public String getDisplayedText()
    {
        String s = "";
        DataValuesSpinnerAdapter spinnerAdapter = (DataValuesSpinnerAdapter) getAdapter();
        if(spinnerAdapter != null)
        {
            WidgetValue v = spinnerAdapter.getItem(getSelectedItemPosition());
            s = v.text;
        }
        return  s;
    }

    @Override
    public void setValidData(boolean valid)
    {
        TextView tv = (TextView) findViewById(R.id.text);
        if(!valid)
            tv.setTextColor(Color.RED);
        else
            tv.setTextColor(Color.BLACK);
    }
}
