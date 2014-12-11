package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeDrawerFragment;

public class HomeActivity extends ItActivity implements HomeDrawerFragment.DrawerCallbacks {

	private Toolbar toolbar;
	private HomeDrawerFragment mhomeDrawerFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		setToolbar();
		setDrawer();
	}


	@Override
	public void onDrawerItemSelected(int position) {
	}


	private void setToolbar(){
		toolbar = (Toolbar) findViewById(R.id.home_toolbar);
		setSupportActionBar(toolbar);
	}


	private void setDrawer(){
		mhomeDrawerFragment = (HomeDrawerFragment) getFragmentManager().findFragmentById(R.id.home_drawer);
		mhomeDrawerFragment.setUp(R.id.home_drawer, (DrawerLayout) findViewById(R.id.home_drawer_layout), toolbar);
	}
}
