package it.giacomos.android.wwwsapp.report;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataParser 
{
	public DataParser()
	{
		mErrorMsg = "";
	}

	private String mErrorMsg;

	public String getError()
	{
		return mErrorMsg;
	}

	public DataInterface[] parse(String txt)
	{
		DataInterface[] ret = null;

		try
		{
			JSONObject jso = new JSONObject(txt);
			/* this is me!, not the account name of the other persons' publications */
			String account = jso.getString("account");
			String layer = jso.getString("layer");
			JSONArray data = jso.getJSONArray("data");

			ret = new DataInterface[data.length()];
			for(int i = 0; i < data.length(); i++)
			{
				JSONObject o = data.getJSONObject(i);
				String datetime = o.getString("datetime");
				double lat = o.getDouble("lat");
				double lon = o.getDouble("lon");
				if(o.has("type") && o.getString("type").compareTo("report") == 0) /* it is a report */
				{
					int event_id = o.getInt("event_id");
					String displayName = o.getString("display_name");
					ReportData reportData = new ReportData(event_id, layer, lat, lon, datetime, displayName);
					Iterator<String> keys = o.keys();
					String key;
					while (keys.hasNext())
					{
						key = keys.next();
						reportData.add(key, o.getString(key));
					}
					Log.e("DataI.parse", "layer " + layer + " lat " + lat + " lon " + lon + " datet " + datetime + " disp name " + displayName);
					ret[i] = reportData;
				}
				else if(o.has("type") && o.getString("type").compareTo("active_user") == 0)
				{
					ActiveUser activeUser = new ActiveUser(datetime, lat, lon, true, true, 0);
					ret[i] = activeUser;
				}
			}
		}
		catch (JSONException e)
		{
			Log.e("DataParser.parse", " error: " + e.getLocalizedMessage());
            mErrorMsg = e.getLocalizedMessage();
		}


		return ret;
	}

}