package it.giacomos.android.wwwsapp.widgets.map.report;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.state.Urls;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostActionResultListener;
import it.giacomos.android.wwwsapp.widgets.map.report.network.PostType;
import it.giacomos.android.wwwsapp.widgets.map.report.network.RemovePostTask;
import it.giacomos.android.wwwsapp.widgets.map.report.network.RemovePostTaskListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class RemovePostConfirmDialog extends DialogFragment implements RemovePostTaskListener,
DialogInterface.OnClickListener

{
	private LatLng mLatLng = null;
	private PostType mType;
	private String mDeviceId;
	
	/* HelloWorldActivity wants to be notified whether the task is completed (successfully or not)
	 * or if the dialog is cancelled.
	 */
	private PostActionResultListener mPostActionResultListener;
	
	public void setLatLng(LatLng point)
	{
		mLatLng = point;
	}
	
	/** Sets the PostActionResultListener that waits for the dialog to be canceled or the 
	 * task to be complete. HelloWorldActivity implements this interface.
	 * 
	 * @param parl (HelloWorldActivity)
	 */
	public void setPostActionResultListener(PostActionResultListener parl)
	{
		mPostActionResultListener = parl;
	}

	public void setType(PostType type) {
		mType = type;
	}

	public void setDeviceId(String devid) {
		mDeviceId = devid;
		
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
	{

        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(this.getString(R.string.reportRemoveConfirm))
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, null).create();
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		RemovePostTask removePostTask = new RemovePostTask(mType, mDeviceId, mLatLng.latitude,
        		mLatLng.longitude, this);
		String url = new Urls().getRemovePostUrl();
		removePostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
	}

	@Override
	/** Invokes the method onPostActionResult on the PostActionResultListener implementor.
	 *  HelloWorldActivity implements PostActionResultListener.
	 */
	public void onRemovePostTaskCompleted(boolean error, String message, PostType removePostType) 
	{
		mPostActionResultListener.onPostActionResult(error, message, removePostType);
	}

}
