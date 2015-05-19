package it.giacomos.android.wwwsapp.layers;

import java.util.ArrayList;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class DownloadManagerStatusWatcher implements Runnable
{
	private ArrayList<Integer> mDownloadIds;
	private Context mContext;
	private Handler mHandler;
	private DownloadManagerStatusWatcherListener mWatcherListener;

	public DownloadManagerStatusWatcher(Context ctx, DownloadManagerStatusWatcherListener l)
	{
		mContext = ctx;
		mHandler = new Handler();
		mWatcherListener = l;
	}

	public DownloadManagerStatusWatcher(ArrayList<Integer> downloadIds, 
			Context ctx, DownloadManagerStatusWatcherListener l)
	{
		mDownloadIds = downloadIds;
		mContext = ctx;
		mWatcherListener = l;
	}

	public void addDownloadId(int did)
	{
		mDownloadIds.add(did);
	}

	public void start()
	{
		mHandler.removeCallbacks(this);
		mHandler.postDelayed(this, 1000);
	}

	public void stop()
	{
		mHandler.removeCallbacks(this);
	}

	@Override
	public void run() 
	{
		int totalDownloads = this.mDownloadIds.size();
		int completedDownloads = 0;
		String message;

		DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

		for(int downloadId : this.mDownloadIds)
		{
			DownloadManager.Query q = new DownloadManager.Query();
			q.setFilterById(downloadId);

			Cursor cursor = manager.query(q);
			cursor.moveToFirst();
			int bytes_downloaded = cursor.getInt(cursor
					.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

			int downloadStatusCode = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
			if (downloadStatusCode ==  DownloadManager.STATUS_SUCCESSFUL) 
			{
				mDownloadIds.remove(downloadId);
			}

			final double dl_progress = ((double)bytes_downloaded / 
					(double) bytes_total) * 100.0;

			message = statusMessage(cursor);
			Log.e("DownloadManagerStatusWatcher", message);
			cursor.close();
			
			mWatcherListener.onDownloadStatusUpdate(downloadId, message, 
					downloadStatusCode, dl_progress);
		}
		/* auto stop watching! */
		if(mDownloadIds.size() == 0)
			mHandler.removeCallbacks(this);
	}

	private String statusMessage(Cursor c) {
		String msg = "???";

		switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
		case DownloadManager.STATUS_FAILED:
			msg = "Download failed!";
			break;

		case DownloadManager.STATUS_PAUSED:
			msg = "Download paused!";
			break;

		case DownloadManager.STATUS_PENDING:
			msg = "Download pending!";
			break;

		case DownloadManager.STATUS_RUNNING:
			msg = "Download in progress!";
			break;

		case DownloadManager.STATUS_SUCCESSFUL:
			msg = "Download complete!";
			break;

		default:
			msg = "Download is nowhere in sight";
			break;
		}

		return (msg);
	}


}
