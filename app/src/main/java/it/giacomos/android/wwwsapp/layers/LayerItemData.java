package it.giacomos.android.wwwsapp.layers;

import android.graphics.drawable.BitmapDrawable;

public class LayerItemData 
{	
	public void selectiveCopyFrom(LayerItemData other)
	{
		if(other.name != null && !other.name.isEmpty())
			name = other.name;
		if(other.title != null && !other.title.isEmpty())
			title = other.title;
		if(other.short_desc != null && !other.short_desc.isEmpty())
			short_desc = other.short_desc;
		if(other.author != null && !other.author.isEmpty())
			author = other.author;
	//	if(other.installed_version != 0)
	//		installed_version = other.installed_version;
		if(other.available_version != 0)
			available_version = other.available_version;
		if(other.date != null && !other.date.isEmpty())
			date = other.date;
		
		
		if(other.install_date != null && !other.install_date.isEmpty())
			install_date = other.install_date;
		
		install_progress = other.install_progress;
		
	}
	
	public boolean isValid()
	{
		return name.length() > 0;
	}
	
	public LayerItemData()
	{
		name = short_desc = author = date = install_date = "";
		installed_version = available_version = -1.0f;
		install_progress = 100;
		installed = online = false;
	}
	
	public void setIcon(BitmapDrawable b)
	{
		if(icon != null)
			icon.getBitmap().recycle();
		icon = b;
	}
	
	public boolean installed, online;
	public String name, title, short_desc, long_desc, author;
	public float installed_version, available_version;
	public int install_progress;
	public String date, install_date;
	public BitmapDrawable icon;
}
