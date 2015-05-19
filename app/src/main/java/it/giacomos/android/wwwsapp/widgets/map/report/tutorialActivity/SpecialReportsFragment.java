package it.giacomos.android.wwwsapp.widgets.map.report.tutorialActivity;

import it.giacomos.android.wwwsapp.R;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SpecialReportsFragment extends Fragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.special_reports, container,
				false);
		TextView textView = (TextView) rootView
				.findViewById(R.id.tvTutorialSpecialIndividuals);
		textView.setText(Html.fromHtml(getString(R.string.special_reports_text)));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView = (TextView) rootView
				.findViewById(R.id.tvSpecialForCompanies);
		textView.setText(Html.fromHtml(getString(R.string.special_reports_for_companies_text)));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return rootView;
	}
	
}
