package it.giacomos.android.wwwsapp.widgets;

import android.view.View;

public class ShowDelayedAnimatedTextViewRunnable implements Runnable {

	private final View mView;
	
	public ShowDelayedAnimatedTextViewRunnable(View view)
	{
		mView = view;
	}
	
	@Override
	public void run() 
	{
		mView.setVisibility(View.VISIBLE);
	}

}
