package it.giacomos.android.wwwsapp.widgets.map;

public abstract interface OOverlayInterface 
{
	public abstract void clear();
	
	public abstract int type();
	
	public abstract void hideInfoWindow();
	
	public abstract boolean isInfoWindowVisible();
}
