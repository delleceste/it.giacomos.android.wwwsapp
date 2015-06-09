package it.giacomos.android.wwwsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by giacomo on 9/06/15.
 */
public class PostDataServiceBroadcastReceiver extends BroadcastReceiver
{

    public interface PostDataServiceBroadcastReceiverListener
    {
        void onBroadcastMessageReceived(Bundle data);
    }

    private PostDataServiceBroadcastReceiverListener mListener;

    public PostDataServiceBroadcastReceiver(PostDataServiceBroadcastReceiverListener l)
    {
        super();
        mListener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle data = intent.getExtras();
        if(mListener != null)
            mListener.onBroadcastMessageReceived(data);
    }

    public void unregisterListener()
    {
        mListener = null;
    }

}
