package it.giacomos.android.wwwsapp.widgets;

import it.giacomos.android.wwwsapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView {

	public AnimatedImageView(Context context) 
	{
		super(context);
		mErrorFlag = false;
		this.setVisibility(View.GONE);
	}

	public AnimatedImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mErrorFlag = false;
		this.setVisibility(View.GONE);
	}

	public void hide()
	{
		if(!mErrorFlag)
		{
			this.clearAnimation();
			this.setImageResource(0);
			this.setVisibility(View.GONE);
		}
	}

	public void displayError()
	{
		mErrorFlag = true;
		this.clearAnimation();
		this.setImageResource(R.drawable.ic_menu_task_attention);
	}

	public void resetErrorFlag()
	{
		mErrorFlag = false;
	}

	public void start()
	{
		if(this.getAnimation() == null && !mErrorFlag)
		{
//			Log.e("start() in AnimatedImageView", "starting animation............... " + toString());
			this.setVisibility(View.VISIBLE);
			this.setImageResource(R.drawable.spinner_20_inner_holo);
			Animation anim = AnimationUtils.loadAnimation(getContext(), R.drawable.animated_refresh_actionbar_image);
			startAnimation(anim);
		}
	}

	private boolean mErrorFlag;
}
