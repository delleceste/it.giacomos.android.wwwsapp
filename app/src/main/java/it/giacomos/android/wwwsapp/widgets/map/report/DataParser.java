package it.giacomos.android.wwwsapp.widgets.map.report;

import java.util.ArrayList;
import android.util.Log;

public class DataParser 
{
	public DataInterface[] parse(String txt)
	{
		DataInterface[] ret = null;
		ArrayList<DataInterface> tmpArray = new ArrayList<DataInterface>();

		if(txt.length() > 0)
		{
			/* a line is made up like this 
			 * 2013-11-30 12:07:42::giacomo::45.6525389::13.7837237::1::1::8.3::a b c
			 * ReportData(String u, String d, String l, String c, String t, int s, int w)
			 */
			String [] lines = txt.split("\n");
			String line;
			double lat, lon;
			int sky, wind;
			int numberOfUsersInArea;

			for(int i = 0; i < lines.length; i++)
			{
				line = lines[i];
				if(line.startsWith("R::")) /* report */
				{
					ReportData rd = null;
					String [] parts = line.split("::", -1);
					//	Log.e("DataParser.parseReports", line + ", " +parts.length);

					sky = wind = -1;
					if(parts.length > 10) /* should be 11, since locality has been added */
					{
						try
						{
							sky = Integer.parseInt(parts[6]);
							wind = Integer.parseInt(parts[7]);
						}
						catch(NumberFormatException e)
						{
							Log.e("ReportDataFactory: error converting sky and wind indexes", e.toString());
						}
						try{
							lat = Float.parseFloat(parts[4]);
							lon = Float.parseFloat(parts[5]);
							/*
							 * ReportData(String user, String datet, String l, String c, 
							 * String t, int s, int w, double lat, double longi, String writa)
							 */
							/* a "-" for the locality for now */
							/* parts[1] is writable */
							rd = new ReportData(parts[3], parts[2], parts[10], parts[9], parts[8], 
									sky, wind, lat, lon, parts[1]);
							tmpArray.add(rd);
						}
						catch(NumberFormatException e)
						{
							Log.e("ReportDataFactory: error getting latitude or longitude", e.toString());
						}
					}
					if(rd == null) /* a parse error occurred: invalidate all document parsing */
					{
						tmpArray.clear();
						break;
					}
				}
				else if(line.startsWith("Q::")) /* request */
				{
					RequestData reqd = null;
					String [] parts = line.split("::", -1);
					//	Log.e("DataParser.parseRequests", line + ", " +parts.length);

					if(parts.length > 6) /* should be 7 */
					{
						try{
							lat = Double.parseDouble(parts[4]);
							lon = Double.parseDouble(parts[5]);
							/* parts[1] is writable */
							/* RequestData(String d, String user, double la, double lo, String wri, boolean isSatisfied) */
							reqd = new RequestData(parts[2], parts[3], parts[6], lat, lon, parts[1], true);
							tmpArray.add(reqd);
						}
						catch(NumberFormatException e)
						{
							Log.e("DataParser.parse: error getting latitude or longitude", e.toString());
						}


					}
					if(reqd == null) /* a parse error occurred: invalidate all document parsing */
					{
						tmpArray.clear();
						break;
					}
				}
				/* U:: must be (and are) the last lines in the txt */
				else if(line.startsWith("U::")) /* active user data */
				{
					boolean isRecent = false, isQuiteRecent = false;
					/* U::2014-01-30 15:08:01::45.7058206::13.8586323::1::1 
					 * 
					 * Active user constructor:
					 * ActiveUser(String datet, double lat, double lon, 
					 * boolean recent, boolean quite_recent)
					 */
					ActiveUser activeUser = null;
					String [] parts = line.split("::", -1);
					if(parts.length > 6) /* should be 7 starting from 2.6.3 */
					{
						try{
							lat = Double.parseDouble(parts[2]);
							lon = Double.parseDouble(parts[3]);

							isRecent = (Integer.parseInt(parts[4]) == 1);
							isQuiteRecent = (Integer.parseInt(parts[5]) == 1);
							numberOfUsersInArea = Integer.parseInt(parts[6]);
							activeUser = new ActiveUser(parts[1], lat, lon, isRecent, 
									isQuiteRecent, numberOfUsersInArea);
							tmpArray.add(activeUser);
						}
						catch(NumberFormatException e)
						{
							Log.e("DataParser.parse: error parsing ActiveUser lat. or long.", e.toString());
						}

					}
					/* do not check for activeUser == null 
					 * like in the other two cases because in this case it
					 * may happen.
					 */
				} /* ActiveUser (line starting with U::) if branch */
			} /* lines for cycle */
		}

		if(tmpArray.size() > 0)
			ret = tmpArray.toArray(new DataInterface[tmpArray.size()]);

		return ret;
	}

}