package it.giacomos.android.wwwsapp.interfaceHelpers;

import it.giacomos.android.wwwsapp.HelloWorldActivity;
import it.giacomos.android.wwwsapp.R;
import it.giacomos.android.wwwsapp.widgets.AnimatedImageView;

import java.lang.String;

import android.content.Context;
import android.widget.Toast;

public class ToastMessageManager 
{
	public static Toast mToast;
	
	public void onError(HelloWorldActivity a, String message)
	{
		if(mToast == null)
			mToast  = Toast.makeText(a.getApplicationContext(), a.getResources().getString(R.string.netErrorToast) + "\n", Toast.LENGTH_LONG);
		else
			mToast.setText(a.getResources().getString(R.string.netErrorToast));
		
		mToast.show();
		/* error may arrive before onPrepareOptionsMenu and so check for null */
		AnimatedImageView raiv = a.getRefreshAnimatedImageView();
		if(raiv != null)
			raiv.displayError();
	}
	
	public void onShortMessage(Context ctx, int messageId)
	{
		String message = ctx.getString(messageId);
		if(mToast == null)
			mToast  = Toast.makeText(ctx, message + "\n", Toast.LENGTH_SHORT);
		else
			mToast.setText(message);
		
		mToast.show();
	}

	public void onMessage(Context ctx, int messageId)
	{
		String message = ctx.getString(messageId);
		if(mToast == null)
			mToast  = Toast.makeText(ctx, message + "\n", Toast.LENGTH_LONG);
		else
			mToast.setText(message);
		
		mToast.show();
	}
	
	public void onShortMessage(Context ctx, String message)
	{
		if(mToast == null)
			mToast  = Toast.makeText(ctx, message + "\n", Toast.LENGTH_SHORT);
		else
			mToast.setText(message);
		
		mToast.show();
	}
	
	public void onMessage(Context ctx, String message)
	{
		if(mToast == null)
			mToast  = Toast.makeText(ctx, message + "\n", Toast.LENGTH_LONG);
		else
			mToast.setText(message);
		
		mToast.show();
	}

}
