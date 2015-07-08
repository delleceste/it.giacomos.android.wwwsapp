package it.giacomos.android.wwwsapp.widgets.map;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.widgets.AnimatedView;

/**
 * Created by giacomo on 2/07/15.
 */
public class ContextualMenu implements View.OnClickListener
{

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.buttonAsk)
        {
            mContextualMenuListener.onContextMenuButtonClicked(ContextualMenuListener.Type.REQUEST);
        }
        else if(v.getId()  == R.id.buttonPublish)
        {
            mContextualMenuListener.onContextMenuButtonClicked(ContextualMenuListener.Type.REPORT);
        }
    }

    public interface ContextualMenuListener
    {
        enum Type { REPORT, REQUEST };
        void onContextMenuButtonClicked(Type type);
    }

    private ContextualMenuListener mContextualMenuListener;
    private AnimatedView[] mMenuAnimatedViews;

    public ContextualMenu(Activity a, ViewGroup parent)
    {
        mMenuAnimatedViews = new AnimatedView[2];
        /* [0] top */
        mMenuAnimatedViews[0] = new AnimatedView(a, AnimatedView.Position.TOP);
        parent.addView(mMenuAnimatedViews[0]);
        a.getLayoutInflater().inflate(R.layout.contextual_action_layout_top, mMenuAnimatedViews[0]);
        ((Button) mMenuAnimatedViews[0].findViewById(R.id.buttonAsk)).setOnClickListener(this);

        /* [1] bottom */
        mMenuAnimatedViews[1] = new AnimatedView(a, AnimatedView.Position.BOTTOM);
        parent.addView(mMenuAnimatedViews[1]);
        a.getLayoutInflater().inflate(R.layout.contextual_action_layout_bottom, mMenuAnimatedViews[1]);
        ((Button) mMenuAnimatedViews[1].findViewById(R.id.buttonPublish)).setOnClickListener(this);
    }

    public void setContextualManuListener(ContextualMenuListener l)
    {
        mContextualMenuListener = l;
    }

    public void show()
    {
        for (int i = 0; i < 2; i++)
        {
            if (mMenuAnimatedViews[i] != null)
                mMenuAnimatedViews[i].show();
        }
    }

    public void hide()
    {
        for (int i = 0; i < 2; i++)
        {
            if (mMenuAnimatedViews[i] != null)
                mMenuAnimatedViews[i].hide();
        }
    }

}
