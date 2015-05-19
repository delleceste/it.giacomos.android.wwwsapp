package it.giacomos.android.wwwsapp.network.state;

import android.graphics.Bitmap;

public interface WebcamBitmapTaskListener {
	public void onWebcamBitmapUpdate(Bitmap bmp, String errorMessage);	
	public void onWebcamBitmapBytesUpdate(byte[] mBitmapBytes);
}
