package it.giacomos.android.wwwsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by giacomo on 8/06/15.
 */
public class ErrorActivity  extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if(i != null)
        {
            int title = i.getIntExtra("title", R.string.error_message);
            int text = i.getIntExtra("text", R.string.download_error);
            MyAlertDialogFragment.MakeGenericError(title, text, this);
        }

    }
}
