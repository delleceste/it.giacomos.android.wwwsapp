package it.giacomos.android.wwwsapp;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MyActionBarDrawerToggle extends ActionBarDrawerToggle
{
	private final AppCompatActivity mActivity;
	
	public MyActionBarDrawerToggle(AppCompatActivity a, DrawerLayout drawerLayout,
			int iconDrawer, int iconDrawerOpen, int iconDrawerClose)
	{
		super(a, drawerLayout, iconDrawer, iconDrawerOpen);
		mActivity = a;
	}
	
	/** Called when a drawer has settled in a completely closed state. */
	public void onDrawerClosed(View view) 
	{
		mActivity.supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}

	/** Called when a drawer has settled in a completely open state. */
	public void onDrawerOpened(View drawerView) 
	{
		mActivity.supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}
}
