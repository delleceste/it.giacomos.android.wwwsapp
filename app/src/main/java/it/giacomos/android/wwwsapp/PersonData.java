package it.giacomos.android.wwwsapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.google.android.gms.plus.model.people.Person;

import it.giacomos.android.wwwsapp.layers.FileUtils;

/**
 * Created by giacomo on 9/06/15.
 */
public class PersonData
{
    public String name, account, gplusUrl;

    public PersonData(String acc, String personName, String gplusU)
    {
        account = acc;
        name = personName;
        gplusUrl = gplusU;
    }

    boolean userRegistered(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(account, Context.MODE_PRIVATE);
        return prefs.getBoolean("userRegistered", false);
    }

    /**
     * Marks a successful registration on the remote wwwsapp database.
     *
     * @param ctx
     * @param success true if registration or user data update has been successful, false otherwise
     */
    void setUserRegistered(Context ctx, boolean success)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(account, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean("userRegistered", success);
        e.commit();
    }

    boolean dataChanged(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(account, Context.MODE_PRIVATE);
        if(prefs.getString("personName", "").compareTo(name) != 0 || prefs.getString("personName", "").isEmpty())
            return true;
        if(prefs.getString("gplusUrl", "").compareTo(gplusUrl) != 0)
            return true;
        return false;
    }

    void saveData(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(account, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        if(name != null && name.length() > 0)
		    e.putString("personName", name);
        if(gplusUrl != null && gplusUrl.length() > 0)
		    e.putString("gplusUrl", gplusUrl);
        e.commit();
    }
}
