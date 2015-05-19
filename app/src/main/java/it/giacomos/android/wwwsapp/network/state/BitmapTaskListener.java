package it.giacomos.android.wwwsapp.network.state;

import java.net.URL;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public interface BitmapTaskListener {
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task);	
	public void onBitmapBytesUpdate(byte[] mBitmapBytes, BitmapType bt);
}
