package it.giacomos.android.wwwsapp.layers;

import it.giacomos.android.wwwsapp.network.state.Urls;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class LayerFetchTask extends AsyncTask<Void, LayerFetchTaskProgressData, Boolean> {

	private LayerFetchTaskListener mListener;
	private String mErrorMsg, mAppLang;
	private int mTotal, mAppVersionCode;
	private Context mContext;

	public LayerFetchTask(LayerFetchTaskListener listener, 
			int appVersionCode, String lang, Context ctx)
	{
		mContext = ctx;
		mTotal = 0;
		mListener = listener;
		mAppVersionCode = appVersionCode;
		mAppLang = lang;
		mErrorMsg = "";
	}

	@SuppressLint("NewApi")
	@Override
	protected synchronized Boolean doInBackground(Void ...parame) 
	{
		boolean success = false;
		try{
			int nRead;
			int progress = 0;
			int total = 0;
			int percent = 0;
			byte[] bytes;
			FileUtils cache = new FileUtils();
			Urls myUrls = new Urls();
			URL url = new URL(myUrls.layersListUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);

			String xml = "";
			
			String data = URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode (mAppLang, "UTF-8");
			data += "&" + URLEncoder.encode("app_version", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mAppVersionCode), "UTF-8");

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			wr.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String currentLine;
			while((currentLine = in.readLine()) != null)
				xml += currentLine + "\n";
			
			/* check if layer list did not change */
//			String prevXml = cache.loadFromStorage(LayerListActivity.CACHE_LIST_DIR + "layerlist.xml", mContext);
//			if(prevXml.compareTo(xml) == 0)
//			{
//				Log.e("LayerFetchTask.doInBackground", "* xml layer list didn't change since last check");
//				return null;
//			}
			cache.saveToStorage(xml.getBytes(), LayerListActivity.CACHE_LIST_DIR + "layerlist.xml", mContext);
			in.close();

			if(!isCancelled())
			{
				XmlParser parser = new XmlParser();
				ArrayList<LayerItemData> d = parser.parseLayerList(xml);
				total = d.size();
				Log.e("LayerFetchtask", "detected " + total + " layers from " + xml + " query " + data);
				url = new URL(myUrls.layerDescUrl());
				for(LayerItemData i : d)
				{
					if(isCancelled())
						break;
					xml = "";
					String layer_name = i.name;
					/* get layer description */
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					data = URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode(mAppLang, "UTF-8");
					data += "&" + URLEncoder.encode("layer", "UTF-8") + "=" + URLEncoder.encode(layer_name, "UTF-8");
					Log.e("LayerFetchTask.doInBacgkruod", "fetching" + data);
					wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();
					wr.close();
					in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while((currentLine = in.readLine()) != null)
						xml += currentLine + "\n";
						
					cache.saveToStorage(xml.getBytes(), LayerListActivity.CACHE_LIST_DIR + layer_name + ".xml", mContext);
					Log.e("LayerFetchTask.doInBacgkruod", "parsing layer XML" + xml);
					LayerItemData itemData = parser.parseLayerDescription(xml);
					if(itemData.available_version < 0)
						itemData.available_version = i.available_version;
					
					/* get icon */
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					data = URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode(mAppLang, "UTF-8");
					data += "&" + URLEncoder.encode("layer", "UTF-8") + "=" + URLEncoder.encode(layer_name, "UTF-8");
					data += "&" + URLEncoder.encode("icon", "UTF-8") + "=" + URLEncoder.encode("true", "UTF-8");
					wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();
					wr.close();
					
					InputStream inputStream = conn.getInputStream();
	        		/* get bytes from input stream */
	        		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
	        		bytes = new byte[1024];
	        		while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
	        			byteBuffer.write(bytes, 0, nRead);
	        		}
	        		byte[] mBitmapBytes = byteBuffer.toByteArray();
	        		BitmapFactory.Options o2 = new BitmapFactory.Options();
	                o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        		Bitmap bitmap = BitmapFactory.decodeByteArray(mBitmapBytes, 0, mBitmapBytes.length, o2);
	        		/* a network error may determine decodeByteArray to return a null bitmap (for instance connecting
	        		 * to a unauthenticated wireless network... it happened at Elettra...)
	        		 */
	        		if(bitmap == null) /* prevent from calling onBitmapBytesUpdate */
	        			Log.e("LayerFetchTask.doInBackground", "Error decoding bitmap for layer " + layer_name);
	        		else
	        		{
	        			cache.saveBitmapToStorage(mBitmapBytes, LayerListActivity.CACHE_LIST_DIR + layer_name + ".bmp", mContext);
	        			Log.e("LayerFetchTask.doInBackground", " creating bitmap drawable for " + bitmap + 
	        					": " + bitmap.getWidth() + "x" + bitmap.getHeight());
	        		}
	        		progress++;
	        		
					conn.disconnect();
	        		byteBuffer.flush();
	        		inputStream.close();
	        		percent = (int) Math.round((float) progress / (float) total * 100.0);
	        		Log.e("LayerFetchTask.doInBackground", " publishing progress " + percent);
	        		this.publishProgress(new LayerFetchTaskProgressData(i.name, i.available_version, percent));
				}
				if(!isCancelled())
					success =  true;
			}
		}
		catch (UnsupportedEncodingException e) 
		{
			success = false;
			mErrorMsg = e.getLocalizedMessage();
		} 
		catch (IOException e) 
		{
			success = false;
			mErrorMsg = e.getLocalizedMessage();
		}
		Log.e("LayerFetchTask", " Done!error : " + mErrorMsg);
		
		return success;
	}

	/** Invokes the onTextUpdate method of TextUpdateListener.
	 * The text passed inside onTextUpdate is the downloaded text or an empty string
	 * if the download was not successful.
	 * Error message is passed if an error occurred.
	 */
	@Override
	public void onPostExecute(Boolean success)
	{
		mListener.onLayersUpdated(success, mErrorMsg);
		/* call onLayerFetchProgress with an empty LayerFetchTaskProgressData */
		// mListener.onLayerFetchProgress(new LayerFetchTaskProgressData(), 100);
	}

	@Override
	public void onCancelled()
	{
		mListener.onLayerFetchCancelled();
	}

	@Override
	public void onProgressUpdate(LayerFetchTaskProgressData... values)
	{
		mListener.onLayerFetchProgress(values[0]);
	}

	String errorMessage() 
	{ 
		return mErrorMsg; 
	}

}
