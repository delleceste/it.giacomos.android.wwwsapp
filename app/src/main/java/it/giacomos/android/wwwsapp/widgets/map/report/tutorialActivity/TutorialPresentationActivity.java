package it.giacomos.android.wwwsapp.widgets.map.report.tutorialActivity;

import it.giacomos.android.wwwsapp.R;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

public class TutorialPresentationActivity extends ActionBarActivity 
implements ReportConditionsAcceptedListener
{
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	private boolean mConditionsAccepted;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial_presentation);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		PagerTabStrip pstrip = (PagerTabStrip) mViewPager.findViewById(R.id.pager_title_strip);
		pstrip.setTextColor(Color.BLACK);
		pstrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		Intent i = getIntent();
		mConditionsAccepted = i.getBooleanExtra("conditionsAccepted", false);
		
		Intent resultI = new Intent();
		resultI.putExtra("conditionsAccepted", mConditionsAccepted);
		setResult(Activity.RESULT_OK, resultI);
		if(mConditionsAccepted)
		{
			mViewPager.setCurrentItem(1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial_presentation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			Bundle args = new Bundle();
			if(position == 0)
			{
				fragment = new TermsAndConditionsFragment();
				args.putBoolean("conditionsAccepted", mConditionsAccepted);
			}
			if(position == 1)
			{
				fragment = new QuickStartReportFragment();
			}
			if(position == 2)
			{
				fragment = new QuickStartRequestFragment();
			}
			if(position == 3)
			{
				fragment = new QuickStartNotificationsFragment();
			}
			if(position == 4)
			{
				fragment = new SpecialReportsFragment();
			}
			
			args.putInt("index", position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 5 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.tutorial_tab1_title).toUpperCase(l);
			case 1:
				return getString(R.string.tutorial_tab2_title).toUpperCase(l);
			case 2:
				return getString(R.string.tutorial_tab3_title).toUpperCase(l);
			case 3:
				return getString(R.string.tutorial_tab4_title).toUpperCase(l);
			case 4:
				return getString(R.string.tutorial_tab5_title).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public void onReportConditionsAccepted(boolean accepted) 
	{
		Intent i = new Intent();
		mConditionsAccepted = accepted;
		i.putExtra("conditionsAccepted", accepted);
		setResult(RESULT_OK, i);
	}
}
