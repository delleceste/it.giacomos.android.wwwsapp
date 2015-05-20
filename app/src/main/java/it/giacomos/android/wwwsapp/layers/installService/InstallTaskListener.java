package it.giacomos.android.wwwsapp.layers.installService;

public interface InstallTaskListener 
{
	public void onInstallTaskProgress(String layerName, int percent, InstallTaskState state);
		
	public void onInstallTaskCancelled(String layerName);
	
	public void onInstallTaskCompleted(String layerName, String errorMessage);
}
