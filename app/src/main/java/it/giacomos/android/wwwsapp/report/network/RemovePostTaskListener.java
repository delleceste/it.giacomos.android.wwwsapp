package it.giacomos.android.wwwsapp.report.network;

/** RemovePostConfirmDialog implements this interface */
public interface RemovePostTaskListener 
{
	public void onRemovePostTaskCompleted(boolean error, String message, PostType removePostType);
}
