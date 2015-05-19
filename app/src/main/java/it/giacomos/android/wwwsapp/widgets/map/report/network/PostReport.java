package it.giacomos.android.wwwsapp.widgets.map.report.network;

import it.giacomos.android.wwwsapp.network.state.Urls;
import android.os.AsyncTask;
import android.util.Log;

/** This class manages posting a report on the server.
 *  Since it is launched by a dialog instantiated by the main activity.
 *  The async task PostReportTask makes use of the PostReportAsyncTaskPool
 *  to manage its cancellation when HelloWorldActivity is destroyed.
 *  
 *  @see PostReportTask
 *  
 * @author giacomo
 *
 */
public class PostReport implements PostReportTaskListener, PostInterface
{
	private PostActionResultListener mReportPublishedListener;
	
	public PostReport(String user, String deviceId, String registrationId, String locality, 
			double lat, double lng, int sky, int wind, 
			String temp, String comment,  PostActionResultListener lis)
	{
		mReportPublishedListener = lis;
		PostReportTask postReportTask = new PostReportTask(user, deviceId, registrationId, 
				locality, lat, lng, sky, wind, temp, comment, this);
		String url = new Urls().postReportUrl();
		postReportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
	}

	@Override
	public void onTaskCompleted( boolean error, String message) 
	{
		mReportPublishedListener.onPostActionResult(error, message, PostType.REPORT);
	}
	
	@Override
	public PostType getType() {
		return PostType.REPORT;
	}

}
