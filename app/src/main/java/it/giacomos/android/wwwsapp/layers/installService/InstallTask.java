package it.giacomos.android.wwwsapp.layers.installService;

import it.giacomos.android.wwwsapp.layers.LayerItemData;
import it.giacomos.android.wwwsapp.network.state.Urls;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class InstallTask extends AsyncTask<Void, Integer, String> 
{
	private String mInstallDirName;
	private InstallTaskListener mInstallTaskListener;
	private String mLayerName;
	private String mAppLang;
	private String mErrorMsg;

	public InstallTaskState state;

	public InstallTask(InstallTaskListener l, Context ctx, String layerName)
	{
		mLayerName = layerName;
		mInstallTaskListener = l;
		File filesDir = ctx.getFilesDir();
		mInstallDirName = filesDir.getAbsolutePath() + "/layers/" + layerName;
		mAppLang  = Locale.getDefault().getLanguage();
	}

	@Override
	protected String doInBackground(Void... vo) 
	{
		final int BUFFER_SIZE = 1024;
		state = InstallTaskState.DOWNLOADING;
		Log.e("InstallTask.doInBackground", "1. starting download of layer " + mLayerName);
		mErrorMsg = "";
		Urls myUrls = new Urls();
		URL url;
		try {
			publishProgress(0);
			
			try {
				Thread.sleep(2550);
				print TASK IN PARALLELO! Se no aspetta quell'altro!
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			url = new URL(myUrls.layerDownloadUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);

			String fileName = mLayerName + ".zip";
			
			
			String params = URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode (mAppLang, "UTF-8");
			params += "&" + URLEncoder.encode("layer", "UTF-8") + "=" + URLEncoder.encode(mLayerName, "UTF-8");
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(params);
			wr.flush();
			wr.close();

			int contentLength = conn.getContentLength();
			String disposition = conn.getHeaderField("Content-Disposition");
			String contentType = conn.getContentType();
			
		//	int responseCode = conn.getResponseCode();
		//	if (responseCode == HttpURLConnection.HTTP_OK) 
		//	{
				InputStream inputStream = conn.getInputStream();
				File installDir = new File(mInstallDirName);
				boolean installDirExists = installDir.exists();
				if(!installDirExists)
					installDirExists = installDir.mkdirs();
				if(installDirExists)
				{
					String filePath = mInstallDirName + "/" + fileName;
					// opens an output stream to save into file
					FileOutputStream outputStream = new FileOutputStream(filePath);
					
					int previousPublishedPercentage = 0;
					int percentage;
					int bytesRead = -1;
					float totBytesRead = 0.0f;
					byte[] buffer = new byte[BUFFER_SIZE];
					while ((bytesRead = inputStream.read(buffer)) != -1) 
					{
						if(isCancelled())
							return "";
						
						totBytesRead += bytesRead;
						outputStream.write(buffer, 0, bytesRead);
						percentage = Math.round(totBytesRead * 100.0f / (float) contentLength);
						if(percentage - previousPublishedPercentage >= 5)
						{
							publishProgress(percentage);
							previousPublishedPercentage = percentage;
						}
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					outputStream.close();

					state = InstallTaskState.INSTALLING;
				}
				else
				{
					mErrorMsg = "InstallTask: failed to create directory \"" + mInstallDirName + "\"";
					state = InstallTaskState.INSTALL_ERROR;
				}
				inputStream.close();
				conn.disconnect();

				Log.e("InstallTask.doInBackground", "2. installing layer " + mLayerName +  " into " + mInstallDirName);
				
				if(!unzip(mInstallDirName, fileName))
					mErrorMsg = "Failed to extract layer \"" + mLayerName + "\" into " + mInstallDirName + ": " + mErrorMsg;
				
				state = InstallTaskState.INSTALL_COMPLETE;
				
				return mLayerName;
		} 
		catch (MalformedURLException e) 
		{
			state = InstallTaskState.DOWNLOAD_ERROR;
			Log.e("InstallTask", "MalformedURL exception " + e.getLocalizedMessage());
			mErrorMsg = e.getLocalizedMessage();
		} 
		catch (IOException e) 
		{
			state = InstallTaskState.DOWNLOAD_ERROR;
			Log.e("InstallTask", "IOException exception " + e.getLocalizedMessage());
			mErrorMsg = e.getLocalizedMessage();
		}

		return "";
	}
	
	public boolean unzip(String location, String zipFile) throws IOException 
	{
		final int BUFFER_SIZE = 1024;
	    int size;
	    byte[] buffer = new byte[BUFFER_SIZE];

	    try {
	        if ( !location.endsWith("/") ) {
	            location += "/";
	        }
	        File f = new File(location);
	        if(!f.isDirectory()) {
	            f.mkdirs();
	        }
	        String zipFilePath = location + zipFile;
	        Log.e("InstallTask.unzip", " zip path " + zipFilePath);
	        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFilePath), BUFFER_SIZE));
	        try {
	            ZipEntry ze = null;
	            while ((ze = zin.getNextEntry()) != null) 
	            {
	            	if(isCancelled())
	            	{
	            		zin.close();
	            		return false;
	            	}
	                String path = location + ze.getName();
	                File unzipFile = new File(path);

	                if (ze.isDirectory()) {
	                    if(!unzipFile.isDirectory()) {
	                        unzipFile.mkdirs();
	                    }
	                } else {
	                    // check for and create parent directories if they don't exist
	                    File parentDir = unzipFile.getParentFile();
	                    if ( null != parentDir ) {
	                        if ( !parentDir.isDirectory() ) {
	                            parentDir.mkdirs();
	                        }
	                    }

	                    // unzip the file
	                    FileOutputStream out = new FileOutputStream(unzipFile, false);
	                    BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
	                    try {
	                        while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) 
	                        {
	                            fout.write(buffer, 0, size);
	                        }

	                        zin.closeEntry();
	                    }
	                    finally {
	                        fout.flush();
	                        fout.close();
	                    }
	                }
	            }
	        }
	        finally {
	            zin.close();
	        }
	        return true;
	    }
	    catch (Exception e) {
	        mErrorMsg = "Error extracting data: " + e.getLocalizedMessage();
	        return false;
	    }
	}
	
	@Override
	public void onPostExecute(String layerName)
	{
		mInstallTaskListener.onInstallTaskCompleted(mLayerName, mErrorMsg);
	}

	@Override
	public void onCancelled(String layerName)
	{
		mInstallTaskListener.onInstallTaskCancelled(mLayerName);	
	}

	@Override
	public void onProgressUpdate(Integer... percent)
	{
		mInstallTaskListener.onInstallTaskProgress(mLayerName, percent[0]);
	}

	String errorMessage() 
	{ 
		return mErrorMsg; 
	}

}
