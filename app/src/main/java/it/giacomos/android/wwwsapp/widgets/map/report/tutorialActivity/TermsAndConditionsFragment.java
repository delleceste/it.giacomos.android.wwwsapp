package it.giacomos.android.wwwsapp.widgets.map.report.tutorialActivity;

import it.giacomos.android.wwwsapp.R;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TermsAndConditionsFragment extends Fragment implements  OnCheckedChangeListener
{
	private ReportConditionsAcceptedListener mReportConditionsAcceptedListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(
				R.layout.tutorial_conditions, container,
				false);
		TextView contentTextView = (TextView) rootView
				.findViewById(R.id.tvTermsConditionsCompanies);
		contentTextView.setText(Html.fromHtml(getActivity().
				getResources().getString(R.string.report_terms_and_conditions_text)));
		contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
		
		
		return rootView;
	}

	@Override
	public void   onActivityCreated (Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		CheckBox cb = (CheckBox) getActivity().findViewById(R.id.cbAcceptTermsAndConditions);
		Bundle args = this.getArguments();
		cb.setChecked(args != null && args.getBoolean("conditionsAccepted"));
		cb.setOnCheckedChangeListener(this);
		mReportConditionsAcceptedListener = (ReportConditionsAcceptedListener) getActivity();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton bt, boolean checked) 
	{
		mReportConditionsAcceptedListener.onReportConditionsAccepted(checked);
	}
}
