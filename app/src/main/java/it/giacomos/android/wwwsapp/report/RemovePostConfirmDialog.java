package it.giacomos.android.wwwsapp.report;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.Urls;
import it.giacomos.android.wwwsapp.report.network.PostActionResultListener;
import it.giacomos.android.wwwsapp.report.network.PostType;
import it.giacomos.android.wwwsapp.report.network.RemovePostTaskListener;
import it.giacomos.android.wwwsapp.service.PostDataService;
import it.giacomos.android.wwwsapp.service.PostDataServiceParamsBuilder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class RemovePostConfirmDialog extends DialogFragment implements RemovePostTaskListener,
DialogInterface.OnClickListener

{
	private LatLng mLatLng = null;
	private PostType mType;
	private String mAccount, mLayer;
	
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
	public void onClick(DialogInterface dialog, int which)
	{
		/* Get data from the UI and place it on a HashMap key/value */
		PostDataServiceParamsBuilder pBuilder = new PostDataServiceParamsBuilder();
		pBuilder.add("account", mAccount);
		pBuilder.add("layer", mLayer);
		pBuilder.add("latitude", mLatLng.latitude);
		pBuilder.add("longitude", mLatLng.longitude);

		Intent service_intent = new Intent(getActivity(), PostDataService.class);
		service_intent.putExtra("serviceName", "RemoveRequestService");
		service_intent.putExtra("params", pBuilder.toString());
		service_intent.putExtra("url", new Urls().removeRequestUrl());

		Log.e("RequestDiaFrag.onClick", "starting service PostDataService");
		getActivity().startService(service_intent);
	}

	@Override
	/** Invokes the method onPostActionResult on the PostActionResultListener implementor.
	 *  HelloWorldActivity implements PostActionResultListener.
	 */
	public void onRemovePostTaskCompleted(boolean error, String message, PostType removePostType) 
	{
		mPostActionResultListener.onPostActionResult(error, message, removePostType);
	}

	public void setLayer(String name)
	{
		mLayer = name;
	}

	public void setAccount(String account)
	{
		mAccount = account;
	}
}
