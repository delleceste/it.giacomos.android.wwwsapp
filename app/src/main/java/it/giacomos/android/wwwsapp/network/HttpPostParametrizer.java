package it.giacomos.android.wwwsapp.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by giacomo on 9/06/15.
 */
public class HttpPostParametrizer
{
    private HashMap<String, String> mData;

    public void add(String name, String value)
    {
        if(mData == null)
            mData = new HashMap<String, String> ();
        mData.put(name, value);
    }

    public String toString()
    {
        String s = "";
        for(String key : mData.keySet())
        {
            try
            {
                if(!s.isEmpty())
                    s+= "&";
                s += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(mData.get(key), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return s;
    }
}
