package it.giacomos.android.wwwsapp.report;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.Urls;
import it.giacomos.android.wwwsapp.preferences.Settings;
import it.giacomos.android.wwwsapp.report.network.PostReportRequestTask;
import it.giacomos.android.wwwsapp.report.network.PostType;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.widget.CheckBox;
import android.widget.EditText;

public class ReportRequestDialogClickListener implements OnClickListener {

	private ReportRequestDialogFragment mReportRequestDialogFragment;

	public ReportRequestDialogClickListener(ReportRequestDialogFragment reportRequestDialogFragment) 
	{
		mReportRequestDialogFragment = reportRequestDialogFragment;
	}

	@Override
	public void onClick(DialogInterface dialogI, int whichButton) 
	{
		Dialog d = (Dialog) dialogI;
		HelloWorldActivity oActivity = (HelloWorldActivity) mReportRequestDialogFragment.getActivity();
		LatLng llng = mReportRequestDialogFragment.getLatLng();
		if(llng == null) /* should not be null since the dialog waits for location before enabling the ok button */
			return;
		
		if(whichButton == AlertDialog.BUTTON_POSITIVE)
		{
			String user;
			String locality = "";



		}
		else
		{


			oActivity.onMyReportRequestDialogCancelled(llng);
		}
	}

}
