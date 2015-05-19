package it.giacomos.android.wwwsapp.layers;

import it.giacomos.android.wwwsapp.layers.installService.InstallTaskState;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class LayerItemData 
{		
	public boolean selectiveCopyFrom(LayerItemData other)
	{
		boolean changed = false;
		if(other.name != null && !other.name.isEmpty() && name.compareTo(other.name) != 0)
		{
			name = other.name;
			changed = true;
		}
		if(other.title != null && !other.title.isEmpty() && title.compareTo(other.title ) != 0)
		{
			title = other.title;
			changed = true;
		}
		if(other.short_desc != null && !other.short_desc.isEmpty() && short_desc.compareTo(other.short_desc) != 0)
		{
			short_desc = other.short_desc;
			changed = true;
		}
		if(other.author != null && !other.author.isEmpty() && author.compareTo(other.author) != 0)
		{
			author = other.author;
			changed = true;
		}
		//	if(other.installed_version != 0)
		//		installed_version = other.installed_version;
		if(other.available_version != 0 && available_version != other.available_version)
		{
			available_version = other.available_version;
			changed = true;
		}
		if(other.date != null && !other.date.isEmpty() && date.compareTo(other.date) != 0)
		{
			date = other.date;
			changed = true;
		}

		if(other.install_date != null && !other.install_date.isEmpty() && install_date.compareTo(other.install_date) != 0)
		{
			install_date = other.install_date;
			changed = true;
		}

		if(install_progress != other.install_progress)
		{
			install_progress = other.install_progress;
			changed = true;
		}

		if(installState != other.installState)
		{
			installState = other.installState;
			changed = true;
		}

		if(online != other.online)
		{
			online = other.online;
			changed = true;
		}

		return changed;
	}

	/** This method updates only progress related variables */
	public boolean updateProgress(int installProgress, InstallTaskState instState)
	{
		boolean changed = false;
		if(install_progress != installProgress)
		{
			install_progress = installProgress;
			changed = true;
		}
		if(installState != instState)
		{
			changed = true;
			installState = instState;
		}
		return changed;
	}

	public boolean isValid()
	{
		return name.length() > 0;
	}

	public void restoreProgressInformation(String [] progressInformation)
	{
		if(progressInformation.length == 5)
		{
			name = progressInformation[0];
			installState = InstallTaskState.valueOf(progressInformation[4]);

			try{
				install_progress = Integer.parseInt(progressInformation[1]);
				installed = Boolean.parseBoolean(progressInformation[2]);
				online = Boolean.parseBoolean(progressInformation[3]);
			}
			catch (NumberFormatException e)
			{
				Log.e("LayerItemData: constructor from progress", 
						"NumberFormatException: " + e.getLocalizedMessage());
			}
		}
	}

	public LayerItemData()
	{
		name = short_desc = author = date = install_date = "";
		installed_version = available_version = -1.0f;
		install_progress = 100;
		installed = online = false;
		installState = InstallTaskState.NONE;
	}

	public void setIcon(BitmapDrawable b)
	{
		if(icon != null)
			icon.getBitmap().recycle();
		icon = b;
	}

	public String[] progressInformationToStringArray()
	{
		String [] data = new String[5];
		data[0] = name;
		data[1] = String.valueOf(install_progress);
		data[2] = String.valueOf(installed);
		data[3] = String.valueOf(online);
		data[4] = installState.name();
		return data;
	}

	public boolean installed, online;
	public String name, title, short_desc, long_desc, author;
	public float installed_version, available_version;
	public int install_progress;
	public String date, install_date;
	public BitmapDrawable icon;
	public InstallTaskState installState;
}
