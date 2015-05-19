package it.giacomos.android.wwwsapp.network;

import java.util.Calendar;
import java.util.Date;

import it.giacomos.android.wwwsapp.network.state.BitmapType;
import it.giacomos.android.wwwsapp.network.state.ViewType;

/**
 * Singleton that holds the status of the downloaded data.
 *  
 * @author giacomo
 *
 * This class stores the status of the downloaded data.
 * Each file that is downloaded is associated to a flag that indicates whether
 * it has correctly been downloaded or not.
 * 
 * This class must be a singleton so that until the _process_ is being executed,
 * the state is preserved through screen orientation changes and activity going to
 * background.
 * Being a singleton, its data is preserved across screen orientation changes 
 * without need to restore it explicitly.
 * 
 * The download status is marked as complete when the most relevant data has been
 * downloaded: home, today, tomorrow and 2 days text and today tomorrow and two days
 * symbol table.
 * 
 * Even when the download status is complete, data may be updated if too old
 * (see observationsNeedUpdate method).
 * 
 * When the Online state class is instantiated, it checks if the download
 * is complete and not too old.
 * If it is not complete or it is too old, the download status is put in
 * INIT state, as to indicate that the download is not complete and a 
 * full download starts.
 * Setting status in INIT state (its state is put to 0) means that all
 * XXX_DOWNLOADED flags are discarded.
 * 
 */
public class DownloadStatus {

	private static DownloadStatus _instance = null;

	public static final long DOWNLOAD_OLD_TIMEOUT = 60000;

	public boolean homeDownloaded() { return (state & HOME_DOWNLOADED) != 0; }
	public boolean todayDownloaded() { return (state & TODAY_DOWNLOADED) != 0; }
	public boolean tomorrowDownloaded() { return (state & TOMORROW_DOWNLOADED) != 0; }
	public boolean twoDaysDownloaded() { return (state & TWODAYS_DOWNLOADED) != 0; }
	public boolean threeDaysDownloaded() { return (state & THREEDAYS_DOWNLOADED) != 0; }
	public boolean fourDaysDownloaded() { return (state & FOURDAYS_DOWNLOADED) != 0; }
	public boolean todaySymtableDownloaded() { return (state & TODAY_SYMTABLE_DOWNLOADED) != 0; }
	public boolean tomorrowSymtableDownloaded() { return (state & TOMORROW_SYMTABLE_DOWNLOADED) != 0; }
	public boolean twoDaysSymtableDownloaded() { return (state & TWODAYS_SYMTABLE_DOWNLOADED) != 0; }
	public boolean threeDaysSymtableDownloaded() { return (state & THREEDAYS_SYMTABLE_DOWNLOADED) != 0; }
	public boolean fourDaysSymtableDownloaded() { return (state & FOURDAYS_SYMTABLE_DOWNLOADED) != 0; }
	public boolean latestTableDownloaded() { return (state & LATEST_TABLE_DOWNLOADED) != 0; }
	public boolean downloadErrorCondition() { return (state & DOWNLOAD_ERROR_CONDITION) != 0; }

	public static DownloadStatus Instance()
	{
		if(_instance == null)
			_instance = new DownloadStatus();
		return _instance;
	}

	private DownloadStatus()
	{
		init();
	}

	public void init() {
		state = INIT;
		m_lastUpdateCompletedAt = 0;
	}

	/* suppose it is always necessary to refresh radar image.
	 * 
	 */
	public boolean radarImageDownloaded() { return false;	}
	
	public boolean lastCompleteDownloadIsOld()
	{
		return System.currentTimeMillis() - m_lastUpdateCompletedAt > DOWNLOAD_OLD_TIMEOUT;
	}

	public boolean observationsNeedUpdate()
	{
		Date now = Calendar.getInstance().getTime();
		if((now.getTime() - m_observationsSavedOn.getTime())/1000 > 60)
			return true;
		return false;
	}

	public long lastUpdateCompletedOn() { return m_lastUpdateCompletedAt; }

	public void setLastUpdateCompletedOn(long l) { m_lastUpdateCompletedAt = l; }

	public boolean downloadIncomplete()
	{
		return  !homeDownloaded() || !todayDownloaded() || !tomorrowDownloaded() ||  !twoDaysDownloaded() 
				|| !todaySymtableDownloaded() || !tomorrowSymtableDownloaded() || !twoDaysSymtableDownloaded();
	}

	public boolean downloadComplete() { return !downloadIncomplete(); }


//	public boolean fullForecastDownloadRequested()
//	{
//		return (state &  FORECAST_DOWNLOAD_REQUESTED) != 0;
//	}
//
//	public void setFullForecastDownloadRequested(boolean requested)
//	{
//		if(requested)
//			state = (state | FORECAST_DOWNLOAD_REQUESTED);
//		else
//			state = (state & ~FORECAST_DOWNLOAD_REQUESTED);
//	}

	public void setDownloadErrorCondition(boolean err)
	{
		if(err)
			state = (state | DOWNLOAD_ERROR_CONDITION);
		else
			state = (state & ~DOWNLOAD_ERROR_CONDITION);
	}

	public void updateState(ViewType st, boolean downloaded)
	{
		if(downloaded) 
		{
			if(st == ViewType.TODAY)
				state = (state | TODAY_DOWNLOADED);
			else if(st == ViewType.HOME)
				state = (state | HOME_DOWNLOADED);
			else if(st == ViewType.TOMORROW)
				state = (state | TOMORROW_DOWNLOADED);
			else if(st == ViewType.TWODAYS)
				state = (state | TWODAYS_DOWNLOADED);
			else if(st == ViewType.THREEDAYS)
				state = (state | THREEDAYS_DOWNLOADED);
			else if(st == ViewType.FOURDAYS)
				state = (state | FOURDAYS_DOWNLOADED);
			else if(st == ViewType.TODAY_SYMTABLE)
				state = (state | TODAY_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.TOMORROW_SYMTABLE)
				state = (state | TOMORROW_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.TWODAYS_SYMTABLE)
				state = (state | TWODAYS_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.THREEDAYS_SYMTABLE)
				state = (state | THREEDAYS_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.FOURDAYS_SYMTABLE)
				state = (state | FOURDAYS_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.LATEST_TABLE)
				state = (state | LATEST_TABLE_DOWNLOADED);
		}
		else
		{
			if(st == ViewType.TODAY)
				state = (state & ~TODAY_DOWNLOADED);
			else if(st == ViewType.HOME)
				state = (state & ~HOME_DOWNLOADED);
			else if(st == ViewType.TOMORROW)
				state = (state & ~TOMORROW_DOWNLOADED);
			else if(st == ViewType.TWODAYS)
				state = (state & ~TWODAYS_DOWNLOADED);
			else if(st == ViewType.THREEDAYS)
				state = (state & ~THREEDAYS_DOWNLOADED);
			else if(st == ViewType.FOURDAYS)
				state = (state & ~FOURDAYS_DOWNLOADED);
			else if(st == ViewType.TODAY_SYMTABLE)
				state = (state & ~TODAY_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.TOMORROW_SYMTABLE)
				state = (state & ~TOMORROW_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.TWODAYS_SYMTABLE)
				state = (state & ~TWODAYS_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.THREEDAYS_SYMTABLE)
				state = (state & ~THREEDAYS_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.FOURDAYS_SYMTABLE)
				state = (state & ~FOURDAYS_SYMTABLE_DOWNLOADED);
			else if(st == ViewType.LATEST_TABLE)
				state = (state & ~LATEST_TABLE_DOWNLOADED);
		}

		if(!downloaded)
			setDownloadErrorCondition(true);
		else if(downloadComplete())
		{
			setDownloadErrorCondition(false);
			m_lastUpdateCompletedAt = System.currentTimeMillis();
		}
	}

	public void updateState(BitmapType bt, boolean downloaded)
	{
		if(downloaded)
		{
			if(bt == BitmapType.RADAR)
				state = (state | RADAR_IMAGE_DOWNLOADED);
		}
		else
		{
			if(bt == BitmapType.RADAR)
				state = (state & ~RADAR_IMAGE_DOWNLOADED);
		}
		if(!downloaded)
			setDownloadErrorCondition(true);
		else if(downloadComplete())
		{
			setDownloadErrorCondition(false);
			m_lastUpdateCompletedAt = System.currentTimeMillis();
		}
	}

	public long state;
	public boolean isOnline;
	
	private long m_lastUpdateCompletedAt;

	public  static final long INIT = 0x0;

	/* text related */
	public static final long HOME_DOWNLOADED = 0x01;
	public static final long TODAY_DOWNLOADED = 0x02;
	public static final long TOMORROW_DOWNLOADED = 0x04;
	public static final long TWODAYS_DOWNLOADED = 0x08;
	public static final long TODAY_SYMTABLE_DOWNLOADED = 0x10;
	public static final long TOMORROW_SYMTABLE_DOWNLOADED = 0x20;
	public static final long TWODAYS_SYMTABLE_DOWNLOADED = 0x40;
	/* bitmap related */
	public static final long RADAR_IMAGE_DOWNLOADED = 0x80;

	public static final long FORECAST_DOWNLOAD_REQUESTED = 0x100;
	public static final long LATEST_TABLE_DOWNLOADED = 0x101;
	public static final long THREEDAYS_DOWNLOADED = 0x2000;
	public static final long FOURDAYS_DOWNLOADED = 0x4000;
	public static final long FOURDAYS_SYMTABLE_DOWNLOADED = 0x8000;
	public static final long THREEDAYS_SYMTABLE_DOWNLOADED = 0x10000;

	public static final long DOWNLOAD_ERROR_CONDITION = 0x10000000;

	private Date m_observationsSavedOn;


}
