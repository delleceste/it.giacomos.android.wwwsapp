package it.giacomos.android.wwwsapp.layers;

public class LayerFetchTaskProgressData {
	public LayerFetchTaskProgressData(String nam, float available_ver, int perce) 
	{
		name = nam;
		available_version = available_ver;
		percent = perce;
	}
	
	public LayerFetchTaskProgressData()
	{
		name = "";
		available_version = -1.0f;
	}
	
	public boolean isEmpty()
	{
		return name.length() == 0;
	}
	
	public int percent;
	public String name;
	public float available_version;
}
