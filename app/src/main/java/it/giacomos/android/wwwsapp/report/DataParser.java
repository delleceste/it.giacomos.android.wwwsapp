package it.giacomos.android.wwwsapp.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
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

	public HashMap<String , DataInterface> parse(String layerName, String txt, Context ctx, String ui_type, HashMap<String, DataInterface>  currentData)
	{
		HashMap<String , DataInterface> ret = null;
		String s, id;
		DataInterface dataI = null;
		try
		{
			XmlUiParser xmlUiParser = new XmlUiParser();
			XmlUIDocumentRepr xmlUIDocumentRepr = xmlUiParser.parse(layerName, ctx, ui_type);
			JSONObject jso = new JSONObject(txt);
			/* this is me!, not the account name of the other persons' publications */
			String account = jso.getString("account");
			String layer = jso.getString("layer");
			JSONArray data = jso.getJSONArray("data");

			ret = new HashMap<String , DataInterface>(data.length());
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
						s = o.getString(key);
						if(s != null && !s.isEmpty())
							reportData.add(key, s);
					}
					Log.e("DataI.parse", "layer " + layer + " lat " + lat + " lon " + lon + " datet " + datetime + " disp name " + displayName);
					dataI = reportData;
				}
				else if(o.has("type") && o.getString("type").compareTo("active_user") == 0)
				{
					ActiveUser activeUser = new ActiveUser(datetime, lat, lon, true, true, 0);
					dataI = activeUser;
				}
				else if(o.has("type") && o.getString("type").compareTo("request") == 0)
				{
					String displayName = o.getString("display_name");
					Log.e("DataI.parse", " PROCESSING A REQUEST for " + displayName);
					boolean writable = o.getBoolean("writable");
					boolean isPublished = true;
					RequestData requestData = new RequestData(datetime, displayName, "", lat, lon, writable, isPublished);
					dataI = requestData;
				}
				/* recycle marker if already created for the id */
				if(dataI != null)
				{
					id = dataI.getId();
					if(currentData.containsKey(id)) /* recycle marker */
						dataI.setMarker(currentData.get(id).getMarker());
					else
						dataI.buildMarkerOptions(ctx, xmlUIDocumentRepr);
					ret.put(id, dataI);
				}
			}
		}
		catch (JSONException e)
		{
			Log.e("DataParser.parse", " error: " + e.getLocalizedMessage());
            mErrorMsg = e.getLocalizedMessage();
		}

		if(ret == null)
			ret = new HashMap<String , DataInterface>();
		return ret;
	}

}