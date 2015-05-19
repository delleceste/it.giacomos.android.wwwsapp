package it.giacomos.android.wwwsapp;

import java.util.HashMap;

import android.support.v4.app.FragmentActivity;


public class MyPendingAlertDialog 
{
	
	private MyAlertDialogType mType;
	private int mMsgId;
	private String mMsgStr;
	
	public MyPendingAlertDialog(MyAlertDialogType t, int id)
	{
		mType = t;
		mMsgId = id;
	}
	
	public MyPendingAlertDialog(MyAlertDialogType t, String msg)
	{
		mType = t;
		mMsgId = -1;
		mMsgStr = msg;
	}
	
	public boolean isShowPending()
	{
		return mMsgId > -1;
	}
	
	public void showPending(FragmentActivity a)
	{
		if(mMsgId > -1 && mType == MyAlertDialogType.ERROR)
			MyAlertDialogFragment.MakeGenericError(mMsgId, a);
		else if(mMsgId > -1 && mType == MyAlertDialogType.INFO)
			MyAlertDialogFragment.MakeGenericInfo(mMsgId, a);
		else if(mMsgId < 0 && mType == MyAlertDialogType.ERROR)
			MyAlertDialogFragment.MakeGenericError(mMsgStr, a);
		
		mMsgId = -1; /* cancels isShowPending */
	}
	
}
