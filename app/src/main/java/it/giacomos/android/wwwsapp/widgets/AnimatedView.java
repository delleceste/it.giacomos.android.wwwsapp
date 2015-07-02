package it.giacomos.android.wwwsapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class AnimatedView extends RelativeLayout implements AnimationListener
{
    private enum Mode { SHOWING, HIDING };

    public enum Position { TOP, BOTTOM };

	public final long TIMEOUT = 10L;
    private Mode mMode;
    private Position mPosition;
    private int mInDuration, mOutDuration;

	public AnimatedView(Context context, Position pos)
	{
		super(context);
        init();
        mPosition = pos;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if(mPosition == Position.BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setLayoutParams(params);
	}

    private void init()
    {
        mInDuration = mOutDuration = 500;
        mMode = Mode.SHOWING;
        setVisibility(View.GONE);
    }

    public void setInDuration(int d)
    {
        mInDuration = d;
    }

    public int getInDuration()
    {
        return mInDuration;
    }

    public void setOutDuration(int d)
    {
        mOutDuration = d;
    }

    public int getOutDuration()
    {
        return mOutDuration;
    }

	public void show()
	{
        if(getVisibility() != View.VISIBLE)
        {
            mMode = Mode.SHOWING;
            int y0, y1;
            if(mPosition== Position.TOP)
            {
                y0 = -getHeight();
                y1 = 0;
            }
            else
            {
                y0 = getHeight();
                y1 = 0;
            }
            TranslateAnimation translation = new TranslateAnimation( 0, 0, y0, y1);
            translation.setDuration(mInDuration);
            translation.setAnimationListener(this);
            translation.setFillAfter(true);
            Log.e("AnimatedVIew", "SHOWING");
            this.startAnimation(translation);
        }
	}

	public void hide()
	{
        if(getVisibility() == View.VISIBLE)
        {
            mMode = Mode.HIDING;
            Log.e("AnimatedVIew", "HIDING");
            int y0, y1;
            if(mPosition== Position.TOP)
            {
                y0 = 0;
                y1 = -getHeight();
            }
            else
            {
                y0 = 0;
                y1 = getHeight();
            }
            TranslateAnimation translation = new TranslateAnimation(0, 0, y0, y1);
            translation.setDuration(mOutDuration);
            translation.setAnimationListener(this);
            translation.setFillAfter(true);
            this.startAnimation(translation);
        }
	}

	@Override
	public void onAnimationEnd(Animation animation) 
	{
        Log.e("AnimatedView.onAnimEnd", "mde " + mMode);
        if(mMode == Mode.HIDING)
        {
            setVisibility(View.GONE);
          //  removeAllViews();
        }
	}

	@Override
	public void onAnimationRepeat(Animation animation) 
	{
		
	}

	@Override
	public void onAnimationStart(Animation animation) 
	{
		setVisibility(View.VISIBLE);
	}

	public boolean animationHasStarted() 
	{
		boolean hasStarted = getAnimation() != null && getAnimation().hasStarted();
		return hasStarted;
	}

}
