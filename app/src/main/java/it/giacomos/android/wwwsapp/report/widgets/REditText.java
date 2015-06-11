package it.giacomos.android.wwwsapp.report.widgets;

import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;

public class REditText extends EditText implements TextValueInterface
{

    public REditText(Context context)
    {
        super(context);
    }

    @Override
    public String getDisplayedText()
    {
        return super.getText().toString();
    }

     @Override
    public String getValue()
    {
        return getText().toString();
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

