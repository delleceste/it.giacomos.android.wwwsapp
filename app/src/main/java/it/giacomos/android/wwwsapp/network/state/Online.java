package it.giacomos.android.wwwsapp.network.state;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import it.giacomos.android.wwwsapp.network.DownloadManagerUpdateListener;
import it.giacomos.android.wwwsapp.network.DownloadReason;
import it.giacomos.android.wwwsapp.network.DownloadStatus;
import it.giacomos.android.wwwsapp.widgets.map.MapMode;

public class Online extends State implements BitmapTaskListener, TextTaskListener {

	/** 
	 * When the Online state class is instantiated, it checks if the download
	 * is complete and not too old.
	 * If it is not complete or it is too old, the download status is put in
	 * INIT state, as to indicate that the download is not complete and a 
	 * full download starts.
	 * Setting status in INIT state (its state is put to 0) means that all
	 * XXX_DOWNLOADED flags are discarded.
	 * 
	 * @param stateUpdateListener
	 * @param downloadStatus
	 */
	public Online(DownloadManagerUpdateListener stateUpdateListener,
			DownloadStatus downloadStatus) 
	{
		super(stateUpdateListener, downloadStatus);

		m_urls = new Urls();
		mTotSteps = 0;
		mCurrentStep = 0;
		mMyTasks = new ArrayList<AsyncTask>();
		downloadStatus.isOnline = true;

		
	}

	public StateName name() { return StateName.Online; }

	public String toString() { return name().toString(); }
	
	@Override
	public void onTextUpdate(String s, ViewType vt, String errorMessage, AsyncTask<URL, Integer, String> task) 
	{
		
	}

	public void onTextBytesUpdate(byte [] bytes, ViewType vt)
	{
		if(bytes != null)
			m_downloadManagerUpdateListener.onTextBytesUpdate(bytes, vt);
	}

	@Override
	public void onBitmapBytesUpdate(byte [] bytes, BitmapType bt)
	{
		m_downloadManagerUpdateListener.onBitmapBytesUpdate(bytes, bt);
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task) 
	{			
		dDownloadStatus.updateState(bt, bmp != null);
		m_downloadManagerUpdateListener.onBitmapUpdate(bmp, bt, errorMessage);

		/* publish progress, after DownloadStatus state has been updated */
		mCurrentStep++;
		m_downloadManagerUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
		mProgressNeedsReset();
		mMyTasks.remove(task);
	}

	public void cancelRunningTasks()
	{
		for(int i = 0; i < mMyTasks.size(); i++)
		{
			/* try to cancel the task. At least, onPostExecute is not called 
			 * Calling this method guarantees that onPostExecute(Object) is never invoked.
			 */
//			Log.e("cancelRunningTasks", "state " + mMyTasks.get(i).getStatus());
			mMyTasks.get(i).cancel(false);
		}
		mMyTasks.clear();
	}

	private void startBitmapTask(String urlStr, BitmapType t)
	{
		BitmapTask bitmapTask = new BitmapTask(this, t);
		try{
			URL url = new URL(urlStr);
			bitmapTask.parallelExecute(url);
			mTotSteps++;
			mMyTasks.add(bitmapTask);
		}
		catch(MalformedURLException e)
		{
			onBitmapUpdate(null, t, e.getMessage(), null);
		}

//		Log.e("startBitmapTask", "type " + t + " dTotSteps " + mTotSteps);
	}

	private void startTextTask(String urlStr, ViewType t)
	{
		TextTask textTask = new TextTask(this, t);
		try{
			URL url = new URL(urlStr);
			textTask.parallelExecute(url);
			mTotSteps++;
			mMyTasks.add(textTask);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + urlStr + "\"\n" , t, e.getMessage(), null);
		}
	}

	private boolean mIsTextTaskExecuting(ViewType vt)
	{
		for(AsyncTask<?, ?, ?> at : mMyTasks)
		{
			if(at instanceof TextTask )
			{
				TextTask tt = (TextTask) at;
				if(tt.getType() == vt && tt.getStatus() == AsyncTask.Status.RUNNING)
				{
//					Log.e("mIsTextTaskExecuting", "TextTask " + vt + " is already RUNNING");
					return true;
				}
			}
		}
//		Log.e("mIsTextTaskExecuting", "TextTask " + vt + " is NOT RUNNING");
		return false;	
	}

	private void mProgressNeedsReset()
	{
		if(mCurrentStep == mTotSteps)
		{
			mCurrentStep = mTotSteps = 0;
//			"mProgressNeedsReset", " dTotSteps " + mTotSteps);
		}
	}


	ArrayList<AsyncTask> mMyTasks;
	Urls m_urls;
	int mTotSteps;
	int mCurrentStep;
}
