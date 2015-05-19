package it.giacomos.android.wwwsapp.network.state;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class WebcamBitmapTask extends AsyncTask<URL, Integer, Bitmap>  
{
	/** the constructor */
	public WebcamBitmapTask(WebcamBitmapTaskListener bitmapUpdateListener, int requestedHeight, int requestedWidth)
	{
		mWebcamBitmapTaskListener = bitmapUpdateListener;
		m_errorMessage = "";
		mReqHeight = requestedHeight;
		mReqWidth = requestedWidth;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public final AsyncTask<URL, Integer, Bitmap> parallelExecute (URL... urls)
	{
		return super.executeOnExecutor(THREAD_POOL_EXECUTOR, urls);
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
        		bitmap = decodeSampledBitmapFromByteArray(mBitmapBytes, mReqWidth, mReqHeight);
        		/* a network error may determine decodeByteArray to return a null bitmap (for instance connecting
        		 * to a unauthenticated wireless network... it happened at Elettra...)
        		 */
        		if(bitmap == null) /* prevent from calling onBitmapBytesUpdate */
        		{
        			mBitmapBytes = null;
        			m_errorMessage = "WebcamBitmapTask: error decoding bitmap: invalid bitmap data";
        		}
        	}
        	catch(IOException e)
        	{
        		m_errorMessage = "WebcamBitmapTask IOException: URL: \"" + mUrl.toString() + "\":\n\"" + e.getLocalizedMessage() + "\"";
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
			mWebcamBitmapTaskListener.onWebcamBitmapBytesUpdate(mBitmapBytes);
		mWebcamBitmapTaskListener.onWebcamBitmapUpdate(bmp, m_errorMessage);
	}

	public Bitmap bitmap() 
	{
		return m_bitmap;
	}
	
	public String getUrl()
	{
		if(mUrl != null)
			return mUrl.toString();
		return "No URL";
	}
	
	private Bitmap decodeSampledBitmapFromByteArray(byte[] data,
	        int reqWidth, int reqHeight)
	{
		Bitmap tmpB;
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(data, 0, data.length, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    tmpB = BitmapFactory.decodeByteArray(data, 0, data.length, options);

//	    if(tmpB != null)
//	    	Log.e("decodeSampledBitmapFromByteArray", " returned bmp is actyally " + tmpB.getByteCount()
//	    		+ " bytes were " + data.length + " sample size " + options.inSampleSize);
	    
	    return tmpB;
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

//    Log.e("calculateInSampleSize", "reqW " + reqWidth + ", req H " + reqHeight + " actual w " + width +
//    		"actual h " + height);
    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
	
	private Bitmap m_bitmap; 
	private WebcamBitmapTaskListener mWebcamBitmapTaskListener;
	private String m_errorMessage;
	private URL mUrl;
	private byte[] mBitmapBytes;
	private int mReqHeight;
	private int mReqWidth;
}
