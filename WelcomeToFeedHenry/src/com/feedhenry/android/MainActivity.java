package com.feedhenry.android;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.feedhenry.android.drawer.adapter.NavDrawerListAdapter;
import com.feedhenry.android.drawer.model.NavDrawerItem;
import com.feedhenry.android.fragments.CallCloudFragment;
import com.feedhenry.android.fragments.CloudIntegrationsFragment;
import com.feedhenry.android.fragments.DataBrowserFragment;
import com.feedhenry.android.fragments.HomeFragment;
import com.feedhenry.android.fragments.LocationFragment;
import com.feedhenry.android.fragments.NativeAppInfoFragment;
import com.feedhenry.android.fragments.PushNotificationsFragment;
import com.feedhenry.android.fragments.StatsFragment;

public class MainActivity extends Activity implements
		HomeFragment.OnOptionSelectedListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// drawer menu items
	private String[] navMenuTitles;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpDrawer();
		if (savedInstanceState == null) {
			
			// on first time display view for first nav item
			displayView(0);
			
			// Initialize the connection to FeedHenry cloud
			InitApp task = new InitApp();
			task.execute(this);
		}
	}

	private class InitApp extends AsyncTask<Activity, Void, Void> {

		@Override
		protected Void doInBackground(Activity... params) {
			try {
				MyApplication.initApp(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressLint("NewApi")
	private void setUpDrawer() {
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		
		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
		// Call Cloud
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
		// Push notifications (with a counter example)
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], true, "22"));
		// Location 
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
		// Data Browser
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
		// App Info
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));
		// Cloud Integrations
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6]));
		// Stats
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7]));
		
		mDrawerList.setOnItemClickListener(new DrawerClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems, "fonts/fontawesome-webfont.ttf");
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, 	// nav menu toggle icon
				R.string.app_name, 		// nav drawer open - description for accessibility
				R.string.app_name 		// nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/**
	 * Drawer item click listener
	 * */
	private class DrawerClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Displaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new CallCloudFragment();
			break;
		case 2:
			fragment = new PushNotificationsFragment();
			break;
		case 3:
			fragment = new LocationFragment();
			break;
		case 4:
			fragment = new DataBrowserFragment();
			break;
		case 5:
			fragment = new NativeAppInfoFragment();
			break;
		case 6:
			fragment = new CloudIntegrationsFragment();
			break;
		case 7:
			fragment = new StatsFragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment)
					.addToBackStack(null).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// Implementation of HomeFragment button listener
	@Override
	public void onOptionSelected(int selection) {

		Fragment fragment = null;
		switch (selection) {
		case 0:
			fragment = new CallCloudFragment();
			break;
		case 1:
			fragment = new PushNotificationsFragment();
			break;
		case 2:
			fragment = new LocationFragment();
			break;
		case 3:
			fragment = new DataBrowserFragment();
		break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment)
					.addToBackStack(null).commit();
			mDrawerList.setItemChecked(selection, true);
			mDrawerList.setSelection(selection);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
}
