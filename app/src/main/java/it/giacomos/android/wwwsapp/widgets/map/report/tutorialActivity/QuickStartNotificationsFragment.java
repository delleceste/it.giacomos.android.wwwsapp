package it.giacomos.android.wwwsapp.widgets.map.report.tutorialActivity;

import it.giacomos.android.wwwsapp.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuickStartNotificationsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.tutorial_notifications, container,
				false);
		
		return rootView;
	}
	
}
