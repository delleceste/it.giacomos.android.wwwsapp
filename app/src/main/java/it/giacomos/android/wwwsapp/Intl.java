package it.giacomos.android.wwwsapp;

import android.content.Context;

/**
 * Created by giacomo on 17/06/15.
 */
public class Intl
{
    private final Context mContext;
    private final String mLang;

    public Intl(Context ctx, String lang)
    {
        mContext = ctx;
        mLang = lang;
    }

    /* translate */
    public String tr(String text)
    {
        return text;
    }
}
