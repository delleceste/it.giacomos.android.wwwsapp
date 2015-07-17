package it.giacomos.android.wwwsapp.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by giacomo on 7/17/15.
 */
public class PostDataServiceParamsBuilder
{
    protected HashMap<String, String> mData;

    public PostDataServiceParamsBuilder()
    {
        mData = new HashMap<String, String>();
    }

    public void add(String key, double value)
    {
        mData.put(key, String.valueOf(value));
    }

    public void add(String key, String value)
    {
        mData.put(key, value);
    }

    public String toString()
    {
        String s = "";
        if(mData != null)
        {
            for (String k : mData.keySet())
                try
                {
                    if(!s.isEmpty())
                        s += "&";
                    s += URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(mData.get(k), "UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
        }

        return s;
    }
}
