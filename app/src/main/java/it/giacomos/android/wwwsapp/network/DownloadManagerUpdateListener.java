package it.giacomos.android.wwwsapp.network;

import android.graphics.Bitmap;
import it.giacomos.android.wwwsapp.network.state.BitmapType;
import it.giacomos.android.wwwsapp.network.state.ViewType;

public interface DownloadManagerUpdateListener 
{
	public void onTextUpdate(String txt, ViewType t, String errorMessage);
	public void onBitmapUpdate(Bitmap bmp, BitmapType t, String errorMessage);
	public void onProgressUpdate(int step, int total);
	public void onDownloadStart(DownloadReason reason);
	public void onStateChanged(long oldState, long state);
	public void onBitmapBytesUpdate(byte[] bytes, BitmapType bt);
	public void onTextBytesUpdate(byte[] bytes, ViewType vt);
}
