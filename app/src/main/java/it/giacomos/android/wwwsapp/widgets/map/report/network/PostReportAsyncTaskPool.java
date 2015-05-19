package it.giacomos.android.wwwsapp.widgets.map.report.network;

import java.util.ArrayList;

import android.os.AsyncTask;

/**
 * This class collects all threads whose cancellation is not directly managed by
 * their creators. Typically, they are tasks that are launched to make a post
 * (publish a report or a report request).
 * Note that their cancellation in cancelAll does not interrupt the task if it
 * is already running, so that the post operation can be fulfilled.
 * Simply it is avoided that when the task completes it invokes a callback on a
 * destroyed activity.
 * 
 * @author giacomo
 *
 */
public class PostReportAsyncTaskPool 
{
	private static PostReportAsyncTaskPool _instance;
	
	private ArrayList<AsyncTask<?, ?, ?> > mTasks;
	
	
	public static PostReportAsyncTaskPool Instance()
	{
		if(_instance == null)
			_instance = new PostReportAsyncTaskPool();
		return _instance;
	}

	private PostReportAsyncTaskPool()
	{
		mTasks = new ArrayList<AsyncTask<?, ?, ?> >();
	}
	
	public void registerTask(AsyncTask<?, ?, ?> atask)
	{
		mTasks.add(atask);
	}
	
	public void unregisterTask(AsyncTask<?, ?, ?> atask)
	{
		mTasks.remove(atask);
	}
	
	public void cancelAll()
	{
		for(AsyncTask <?, ?, ?> at : mTasks)
		{
			if(at.getStatus() != AsyncTask.Status.FINISHED)
				at.cancel(false);
		}
		mTasks.clear();
	}

}
