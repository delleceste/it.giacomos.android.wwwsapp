package it.giacomos.android.wwwsapp.widgets.map;


public class MapViewMode 
{
	public MapViewMode()
	{
		isInit = true;
	}
	
	public boolean equals(MapViewMode other)
	{
		return other != null && other.currentMode == this.currentMode &&
			 this.isInit == other.isInit;
	}
	
	public MapViewMode(MapMode oMode)
	{
		currentMode = oMode;
		isInit = false;
	}
	
	public MapMode currentMode = MapMode.RADAR;
	
	/* isInit if true means that the map mode has been set while the google map was not ready yet.
	 */
	public boolean isInit;
}