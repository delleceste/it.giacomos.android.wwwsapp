package it.giacomos.android.wwwsapp.report;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.network.Urls;
import it.giacomos.android.wwwsapp.service.PostDataService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;


public class RequestDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
	private View mDialogView;
	private String mLocality, mAccount, mLayer;
	private LatLng mLatLng;
	private XmlUiHelper mXmlUIHelper;

	public static final RequestDialogFragment newInstance(String layer,
																String locality,
																String userDisplayName,
																String account)
	{
		RequestDialogFragment f = new RequestDialogFragment();
	    Bundle bdl = new Bundle(1);
		bdl.putString("layer", layer);
	    bdl.putString("locality", locality);
		bdl.putString("displayName", userDisplayName);
		bdl.putString("account", account);
	    f.setArguments(bdl);
	    return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		Log.e("ReqDialogFragment", "onCreateDialog");
		Bundle args = getArguments();
		mAccount = args.getString("account");
		mLocality = args.getString("locality");
		mLayer = args.getString("layer");
		this.setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//		builder.setMessage(R.string.reportDialogMessage)
		//		.setTitle(R.string.reportDialogTitle);
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		mDialogView = inflater.inflate(R.layout.request_dialog, null);
		LinearLayout container = (LinearLayout) mDialogView.findViewById(R.id.requestContainerLayout);
		Log.e("onCreateDialog", "searchig container " + container);
		mXmlUIHelper = new XmlUiHelper(getActivity(), container);
		mXmlUIHelper.addTextPlaceHolder("#locality", mLocality);
		mXmlUIHelper.build(mLayer, mLocality, XmlUiHelper.UI_TYPE_REQUEST);

		builder.setTitle(mXmlUIHelper.getTitle());
		builder = builder.setView(mDialogView);

		/* Report! and Cangel buttons */
		builder = builder.setPositiveButton(R.string.reportDialogRequestSendButton, this);

		/* negative button: save the user name */
		builder = builder.setNegativeButton(R.string.reportDialogCancelButton, this);
		// Create the AlertDialog object and return it
		final AlertDialog alertDialog = builder.create();
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Light);
		/* populate Name field with last value */
		String userName = args.getString("displayName");

		TextView tv = (TextView) mDialogView.findViewById(R.id.tvName);
		tv.setText(userName);
//		et.addTextChangedListener(new TextWatcher() {
//
//			public void onTextChanged(CharSequence cs, int start, int before, int count) {}
//
//			@Override
//			public void afterTextChanged(Editable ed) {
//				Log.e("TextWatcher.afterTextChanged", "afterTextChangeth");
//				mCheckUsernameNotEmpty(alertDialog);
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start,
//					int count, int after) {}
//
//		});


//		if(userName.isEmpty())
//			Toast.makeText(getActivity(), R.string.reportMustInsertUserName, Toast.LENGTH_LONG).show();
//
//		mCheckUsernameNotEmpty(alertDialog);
		return alertDialog;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.e("RequestDialogFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		/* register for locality name updates and location updates */
	}

	public void setData(LatLng pointOnMap, String locality)
	{
		Log.e("setData", "called set data with " + locality);
		mLatLng = pointOnMap;
		/* the name obtained by geocode address task in ReportOverlay. May be "-" */
		mLocality = locality;
	}

	public void setLocality(String locality) 
	{
//		if(mDialogView != null && mDialogView.getContext() != null)
//		{
//			TextView textView = (TextView) mDialogView.findViewById(R.id.tvDialogRequestTitle);
//			textView.setText(mDialogView.getContext().getString(R.string.reportDialogRequestTitle) + " " + locality);
//			textView = (TextView) mDialogView.findViewById(R.id.tvRequestLocationName);
//			if(!locality.isEmpty())
//				textView.setText(locality);
//			else
//				textView.setText(this.getResources().getString(R.string.reportDialogRequestLocationUnavailable));
//		}
	}


	public LatLng getLatLng()
	{
		return mLatLng;
	}

	public String getLocality()
	{
		return mLocality;
	}

	@Override
	public void onClick(DialogInterface dialogI, int whichButton)
	{
		boolean cancelled = true;
		Dialog d = (Dialog) dialogI;
		HelloWorldActivity oActivity = (HelloWorldActivity) getActivity();
		LatLng llng = getLatLng();
		if(mLatLng == null) /* should not be null since the dialog waits for location before enabling the ok button */
			return;

		if(whichButton == AlertDialog.BUTTON_POSITIVE)
		{
			String user;
			String locality = "";

            /* Get data from the UI and place it on a HashMap key/value */
            ReportDataBuilder reportDataBuilder = new ReportDataBuilder();
            reportDataBuilder.build(mXmlUIHelper.getData(), getActivity());
            reportDataBuilder.add("account", mAccount);
            reportDataBuilder.add("layer", mLayer);
            reportDataBuilder.add("latitude", mLatLng.latitude);
            reportDataBuilder.add("longitude", mLatLng.longitude);

            Intent service_intent = new Intent(getActivity(), PostDataService.class);
            service_intent.putExtra("serviceName", "RequestService");
            service_intent.putExtra("params", reportDataBuilder.toString());
            service_intent.putExtra("url", new Urls().requestUrl());

            Log.e("RequestDiaFrag.onClick", "starting service PostDataService");
			oActivity.startService(service_intent);
			cancelled = false;
		}

		oActivity.onRequestDialogClosed(llng, cancelled);
	}
}

