package it.giacomos.android.wwwsapp.network.state;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class TextTask extends AsyncTask<URL, Integer, String> {

	/** the constructor */
	public TextTask(TextTaskListener textTaskListener, ViewType t)
	{
		m_textUpdateListener = textTaskListener;
		m_errorMessage = "";
		m_type = t;
		mReferer = null;
	}

	public ViewType getType()
	{
		return m_type;
	}
	
	public void setReferer(String ref)
	{
		mReferer = ref;
	}

	public boolean error()
	{
		return !m_errorMessage.isEmpty();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public final AsyncTask<URL, Integer, String> parallelExecute (URL... urls)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) /* 11 */
			return this.executeOnExecutor(THREAD_POOL_EXECUTOR, urls);
		else
			return this.execute(urls);
	}
	
	/** Invokes the onTextUpdate method of TextUpdateListener.
	 * The text passed inside onTextUpdate is the downloaded text or an empty string
	 * if the download was not successful.
	 * Error message is passed if an error occurred.
	 */
	public void onPostExecute(String doc)
	{
		m_textUpdateListener.onTextUpdate(doc, m_type, m_errorMessage, this);
		if(mTextBytes != null)
			m_textUpdateListener.onTextBytesUpdate(mTextBytes, m_type);
	}

	public void onCancelled(String doc)
	{
		if(doc != null)
			doc = null;
	}
	
	String errorMessage() { return m_errorMessage; }

	protected String doInBackground(URL... urls) 
	{
		String doc = "";
		m_errorMessage = "";
		mTextBytes = null;
		byte [] bytes = null;
		int nRead;
		InputStream inputStream;
		String charset;
		charset = "ISO-8859-1";
		if(urls.length == 1)
		{
			URLConnection urlConnection = null;
			try {
				urlConnection = urls[0].openConnection();
				/* need http referer */
				if(mReferer != null)
					urlConnection.setRequestProperty("Referer", mReferer);
				inputStream = urlConnection.getInputStream();
        		/* get bytes from input stream */
        		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        		bytes = new byte[1024];
				while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
        			byteBuffer.write(bytes, 0, nRead);
        		}
        		byteBuffer.flush();
        		mTextBytes = byteBuffer.toByteArray();
				doc = new String(mTextBytes, charset);
				publishProgress(100);
			} 
			catch (IOException e) 
			{
				doc = null;
				m_errorMessage = "IOException: URL: \"" + urls[0].toString() + "\":\n\"" + e.getLocalizedMessage() + "\"";
			}
		}
		return doc;
	}

	private ViewType m_type;
	private TextTaskListener m_textUpdateListener;
	private String m_errorMessage;
	private String mReferer;
	private byte[] mTextBytes;
}
