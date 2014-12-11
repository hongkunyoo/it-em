package com.pinthecloud.item.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.HomeDrawerMenuListAdapter;

public class HomeDrawerFragment extends ItFragment {

	private RecyclerView mListView;
	private HomeDrawerMenuListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;

	private DrawerCallbacks mCallbacks;
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private View mFragmentContainerView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home_drawer, container, false);
		setList(view);
		return view;
	}


	private void setList(View view){
		mListView = (RecyclerView)view.findViewById(R.id.home_drawer_frag_menu_list);
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mListAdapter = new HomeDrawerMenuListAdapter(mActivity);
		mListView.setAdapter(mListAdapter);
	}


	public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
		mFragmentContainerView = mActivity.findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.home_drawer_shadow, GravityCompat.START);

		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(mActivity,
				mDrawerLayout,
				toolbar,
				R.string.drawer_open,
				R.string.drawer_close
				) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (isAdded()) {
					mActivity.invalidateOptionsMenu();
				}

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (isAdded()) {
					mActivity.invalidateOptionsMenu();
				}
			}
		};

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (DrawerCallbacks) activity;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	public static interface DrawerCallbacks {
		void onDrawerItemSelected(int position);
	}
}
