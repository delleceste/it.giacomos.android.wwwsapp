/**
 * 
 */
package it.giacomos.android.wwwsapp.network.state;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author giacomo
 *
 */
@SuppressLint("NewApi")
public class BitmapTask extends AsyncTask<URL, Integer, Bitmap> 
{
	/** the constructor */
	public BitmapTask(BitmapTaskListener bitmapUpdateListener, BitmapType bt)
	{
		m_stateUpdateListener = bitmapUpdateListener;
		m_errorMessage = "";
		m_bitmapType = bt;
	}
	
	public BitmapType getType()
	{
		return m_bitmapType;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public final AsyncTask<URL, Integer, Bitmap> parallelExecute (URL... urls)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			return super.executeOnExecutor(THREAD_POOL_EXECUTOR, urls);
		}
		else
		{
			return super.execute(urls);
		}
	}
	
	public boolean error()
	{
		return !m_errorMessage.isEmpty();
	}
	
	protected Bitmap doInBackground(URL... urls) 
	{
		InputStream inputStream;
		Bitmap bitmap = null;
		int nRead;
		byte [] bytes = null;
		/* reset mBitmapBytes to avoid updating an old bitmap if try below fails */
		mBitmapBytes = null;
		m_errorMessage = "";
        if(urls.length == 1)
        {
        	mUrl = urls[0];
        	try
        	{
        		inputStream = (InputStream) mUrl.getContent();
        		/* get bytes from input stream */
        		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        		bytes = new byte[1024];
        		while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
        			byteBuffer.write(bytes, 0, nRead);
        		}
        		byteBuffer.flush();

        		mBitmapBytes = byteBuffer.toByteArray();
        		BitmapFactory.Options o2 = new BitmapFactory.Options();
        		o2.inDither = true;
                o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
        		bitmap = BitmapFactory.decodeByteArray(mBitmapBytes, 0, mBitmapBytes.length, o2);
        		/* a network error may determine decodeByteArray to return a null bitmap (for instance connecting
        		 * to a unauthenticated wireless network... it happened at Elettra...)
        		 */
        		if(bitmap == null) /* prevent from calling onBitmapBytesUpdate */
        		{
        			mBitmapBytes = null;
        			m_errorMessage = "BitmapTask: error decoding bitmap: invalid bitmap data";
        		}
        		else
        		{
            		bitmap.setHasAlpha(true);
        		}
        	}
        	catch(IOException e)
        	{
        		m_errorMessage = "IOException: URL: \"" + mUrl.toString() + "\":\n\"" + e.getLocalizedMessage() + "\"";
        	}
        	publishProgress(100);
        }    
        return bitmap;
	}
	
	public void onCancelled(Bitmap bmp)
	{
		if(bmp != null)
			bmp.recycle();
		bmp = null;
		if(m_bitmap != null)
			m_bitmap.recycle();
		m_bitmap = null;
	}
	
	public void onPostExecute(Bitmap bmp)
	{
		m_bitmap = bmp;
		if(mBitmapBytes != null)
			m_stateUpdateListener.onBitmapBytesUpdate(mBitmapBytes, m_bitmapType);
		m_stateUpdateListener.onBitmapUpdate(bmp, m_bitmapType, m_errorMessage, this);
	}

	public Bitmap bitmap() 
	{
		return m_bitmap;
	}
	
	public BitmapType bitmapType()
	{
		return m_bitmapType;
	}
	
	void setBitmapType(BitmapType bt)
	{
		m_bitmapType = bt;
	}
	
	public String getUrl()
	{
		if(mUrl != null)
			return mUrl.toString();
		return "No URL";
	}
	
	private BitmapType m_bitmapType;
	private Bitmap m_bitmap; 
	private BitmapTaskListener m_stateUpdateListener;
	private String m_errorMessage;
	private URL mUrl;
	private byte[] mBitmapBytes;

}
