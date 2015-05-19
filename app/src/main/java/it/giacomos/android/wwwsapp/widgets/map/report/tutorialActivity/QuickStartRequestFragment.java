package it.giacomos.android.wwwsapp.widgets.map.report.tutorialActivity;

import it.giacomos.android.wwwsapp.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuickStartRequestFragment extends Fragment 
{	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.tutorial_request, container,
				false);
		
		return rootView;
	}
}
