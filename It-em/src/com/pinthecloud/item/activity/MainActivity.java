package com.pinthecloud.item.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.MainDrawerFragment;

public class MainActivity extends ItActivity implements MainDrawerFragment.DrawerCallbacks {

	private Toolbar toolbar;
	private DrawerLayout mDrawerLayout;
	private MainDrawerFragment mDrawerFragment;
	private int mCurrentSelectedPosition;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setToolbar();
		setDrawer();
	}


	@Override
	public void onBackPressed() {
		if(mCurrentSelectedPosition == MainDrawerFragment.HOME_POSITION){
			super.onBackPressed();
		} else {
			mDrawerFragment.selectItem(MainDrawerFragment.HOME_POSITION);
		}
	}


	@Override
	public void onDrawerItemSelected(int position, ItFragment fragment) {
		mCurrentSelectedPosition = position;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.main_container, fragment);
		transaction.commit();
	}


	private void setToolbar(){
		toolbar = (Toolbar) findViewById(R.id.main_toolbar);
		setSupportActionBar(toolbar);
	}


	private void setDrawer(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
		mDrawerFragment = (MainDrawerFragment) getFragmentManager().findFragmentById(R.id.main_drawer);
		mDrawerFragment.setUp(R.id.main_drawer, mDrawerLayout, toolbar);
	}
}
