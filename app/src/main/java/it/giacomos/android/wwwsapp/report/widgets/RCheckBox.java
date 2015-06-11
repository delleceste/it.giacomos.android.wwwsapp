package it.giacomos.android.wwwsapp.report.widgets;

import android.content.Context;
import android.graphics.Color;
import android.widget.CheckBox;

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
    public String getValue()
    {
        if(isChecked())
            return  "true";
        else
            return "false";
    }

    public String getDisplayedText()
    {
        return super.getText().toString();
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
