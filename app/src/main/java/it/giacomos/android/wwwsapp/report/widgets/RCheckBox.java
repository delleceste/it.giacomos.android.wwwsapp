package it.giacomos.android.wwwsapp.report.widgets;

import android.content.Context;
import android.graphics.Color;
import android.widget.CheckBox;
import android.widget.TextView;

import it.giacomos.android.wwwsapp.R;

/**
 * Created by giacomo on 5/06/15.
 */
public class RCheckBox extends CheckBox implements TextValueInterface
{

    public RCheckBox(Context context)
    {
        super(context);
    }

    @Override
    public String getValueAsText()
    {
        if(isChecked())
            return  "true";
        else
            return "false";
    }

    @Override
    public void setValidData(boolean valid)
    {
        if(!valid)
            setTextColor(Color.RED);
        else
            setTextColor(Color.BLACK);
    }
}
