package it.giacomos.android.wwwsapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

public class Logger 
{
	public static void log(String message)
	{
//		File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//	//	Log.e("Logger", "logging on " + f.getAbsolutePath() + "/GreenEnlightenment.Service.txt");
//		PrintWriter out;
//		try {
//			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.ServiceLog.txt", true)));
//			out.append(Calendar.getInstance().getTime().toLocaleString()+ ": " + message + "\n");
//			out.close();
//		} catch (FileNotFoundException e1) 
//		{
//			e1.printStackTrace();
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
