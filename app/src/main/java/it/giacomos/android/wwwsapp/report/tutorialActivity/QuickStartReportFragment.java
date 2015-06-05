package it.giacomos.android.wwwsapp.report.tutorialActivity;

import it.giacomos.android.wwwsapp.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuickStartReportFragment extends Fragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.tutorial_report, container,
				false);
		
		return rootView;
	}
	
}
