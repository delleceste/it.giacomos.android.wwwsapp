package it.giacomos.android.wwwsapp.layers;

import it.giacomos.android.wwwsapp.network.state.Urls;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

public class LayerFetchTask extends AsyncTask<Void, Integer, ArrayList<LayerItemData>> {

	private LayerFetchTaskListener mListener;
	private String mErrorMsg, mAppLang;
	private int mProgress, mTotal, mAppVersionCode;
	ArrayList<LayerItemData> mLayerData;
	private Context mContext;

	public LayerFetchTask(LayerFetchTaskListener listener, 
			int appVersionCode, String lang, Context ctx)
	{
		mContext = ctx;
		mTotal = mProgress = 0;
		mListener = listener;
		mLayerData = new ArrayList<LayerItemData>();
		mAppVersionCode = appVersionCode;
		mAppLang = lang;
		mErrorMsg = "";
	}

	public synchronized ArrayList<LayerItemData>getData()
	{
		return mLayerData;
	}

	@SuppressLint("NewApi")
	@Override
	protected synchronized ArrayList<LayerItemData> doInBackground(Void ...parame) 
	{
		try{
			int nRead;
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
			String prevXml = cache.loadFromStorage(LayerListActivity.CACHE_LIST_DIR + "layerlist.xml", mContext);
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
				mTotal = d.size();
				Log.e("LayerFetchtask", "detected " + mTotal + " layers from " + xml + " query " + data);
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
	        					": " + bitmap.getWidth() + "x" + bitmap.getHeight() + " size " + bitmap.getAllocationByteCount());
	        			//itemData.icon = new BitmapDrawable(mContext.getResources(), bitmap);
	        		}
	        			
					mLayerData.add(itemData);
					conn.disconnect();
	        		byteBuffer.flush();
	        		inputStream.close();
					publishProgress(mLayerData.size(), mTotal);				
				}
			}
			if(isCancelled())
			{
				mListener.onLayerFetchCancelled(mLayerData.size(), mTotal);
			}
		}
		catch (UnsupportedEncodingException e) 
		{
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		}
		Log.e("LayerFetchTask", " safely leaving do in bav");
		return mLayerData;

	}

	/** Invokes the onTextUpdate method of TextUpdateListener.
	 * The text passed inside onTextUpdate is the downloaded text or an empty string
	 * if the download was not successful.
	 * Error message is passed if an error occurred.
	 */
	@Override
	public void onPostExecute(ArrayList<LayerItemData> data)
	{
		mListener.onLayersUpdated(data, mErrorMsg);
		if(data != null)
			mListener.onLayerFetchProgress(data.size(), mTotal);
	}

	@Override
	public void onCancelled(ArrayList<LayerItemData> partialData)
	{
		int size;
		if(partialData != null)
			size = partialData.size();
		else 
			size = 0;
		mListener.onLayerFetchProgress(size, mTotal);
	}

	@Override
	public void onProgressUpdate(Integer... values)
	{
		mListener.onLayerFetchProgress(values[0], mTotal);
	}

	String errorMessage() 
	{ 
		return mErrorMsg; 
	}

}
