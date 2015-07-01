package it.giacomos.android.wwwsapp.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.giacomos.android.wwwsapp.R;

/**
 * Created by giacomo on 1/07/15.
 *
 * This class can be used to make a post to a given Url and retrieve a response from the server
 */
public class HttpWriteRead
{
    public enum ValidityMode { MODE_RESPONSE_VALID_IF_ZERO, MODE_ANY_RESPONSE_VALID };

    private ValidityMode mValidityMode;
    private String mError, mTag, mResponse;

    public HttpWriteRead(String tag)
    {
        mTag = tag;
        mResponse = "";
        mError = "";
        mValidityMode = ValidityMode.MODE_ANY_RESPONSE_VALID;
    }

    public void setValidityMode(ValidityMode m)
    {
        mValidityMode = m;
    }

    public ValidityMode getValidityMode()
    {
        return mValidityMode;
    }

    public String getError()
    {
        return mError;
    }

    public String getResponse()
    {
        return mResponse;
    }

    /**
     *
     * @param urlAsString The url to post/read response
     * @param params the post parameters in the form param1=v1&param2=v2&paramN=vN
     * @return true if read is successful, false otherwise
     */
    public boolean read(String urlAsString, String params)
    {
        mResponse = "";
        mError = "";
        URL url;

        try
        {
            url = new URL(urlAsString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            Log.e("HttpWriteRead.read", "url: " + url.toString() + " - data " + params);
            OutputStreamWriter wr;
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(params);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String currentLine;
            while ((currentLine = in.readLine()) != null)
                mResponse += currentLine + "\n";
            if ((mValidityMode == ValidityMode.MODE_RESPONSE_VALID_IF_ZERO)  && (mResponse.trim().compareTo("0") != 0) ) /* trim trailing \n */
                mError = mResponse;
            in.close();
            Log.e("HttpWriteRead.read", " response \"" + mResponse + "\"");

        }
        catch (MalformedURLException e)
        {
            mError = e.getLocalizedMessage();
        }
        catch (UnsupportedEncodingException e)
        {
            mError = e.getLocalizedMessage();
        }
        catch (IOException e)
        {
            mError = e.getLocalizedMessage();
        }
        /* if there is an error message, prepend the tag */
        if(!mError.isEmpty())
            mError = getClass().getName() + " -> " + mTag + ":" + mError;

        return mError.isEmpty();
    }
}
