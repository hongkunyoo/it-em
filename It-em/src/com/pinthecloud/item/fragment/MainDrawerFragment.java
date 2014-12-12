package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

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
import com.pinthecloud.item.adapter.MainDrawerMenuListAdapter;

public class MainDrawerFragment extends ItFragment {

	public static final int HOME_POSITION = 1;

	private RecyclerView mListView;
	private MainDrawerMenuListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<MainDrawerMenu> mMenuList;

	private DrawerCallbacks mCallbacks;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private View mFragmentContainerView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_main_drawer, container, false);
		setList(view);
		selectItem(HOME_POSITION);	// Home Fragment as a first screen.
		return view;
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


	public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
		mFragmentContainerView = mActivity.findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

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


	private void setList(View view){
		mListView = (RecyclerView)view.findViewById(R.id.main_drawer_frag_menu_list);
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		String[] menuNameList = getResources().getStringArray(R.array.main_drawer_menu_string_array);
		mMenuList = new ArrayList<MainDrawerMenu>();
		mMenuList.add(new MainDrawerMenu(0, "", null, false));
		mMenuList.add(new MainDrawerMenu(R.drawable.launcher, menuNameList[0], new HomeFragment(), false));
		mMenuList.add(new MainDrawerMenu(0, menuNameList[1], null, false));
		mMenuList.add(new MainDrawerMenu(R.drawable.launcher, menuNameList[2], null, false));
		mMenuList.add(new MainDrawerMenu(0, menuNameList[3], null, false));
		mMenuList.add(new MainDrawerMenu(R.drawable.launcher, menuNameList[4], null, false));
		mMenuList.add(new MainDrawerMenu(R.drawable.launcher, menuNameList[5], new SettingsFragment(), false));

		mListAdapter = new MainDrawerMenuListAdapter(mActivity, mThisFragment, mMenuList);
		mListView.setAdapter(mListAdapter);
	}


	public void selectItem(int position) {
		MainDrawerMenu menu = mMenuList.get(position);

		// Activate selected menu with view
		// Deactivate other menu
		for(int i=0 ; i<mMenuList.size() ; i++){

			if(i == position){

				menu.setActivated(true);
				mListAdapter.notifyItemChanged(i);
			} else if(mMenuList.get(i).isActivated()) {

				mMenuList.get(i).setActivated(false);
				mListAdapter.notifyItemChanged(i);
			}
		}

		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}

		if (mCallbacks != null) {
			mCallbacks.onDrawerItemSelected(position, menu.getFragment());
		}
	}


	public static interface DrawerCallbacks {
		void onDrawerItemSelected(int position, ItFragment fragment);
	}


	public class MainDrawerMenu {
		private int menuImage;
		private String menuName;
		private ItFragment fragment;
		private boolean isActivated;

		public int getMenuImage() {
			return menuImage;
		}
		public String getMenuName() {
			return menuName;
		}
		public ItFragment getFragment() {
			return fragment;
		}
		public boolean isActivated() {
			return isActivated;
		}
		public void setActivated(boolean isActivated) {
			this.isActivated = isActivated;
		}

		public MainDrawerMenu(int menuImage, String menuName, ItFragment fragment, boolean isActivated) {
			super();
			this.menuImage = menuImage;
			this.menuName = menuName;
			this.fragment = fragment;
			this.isActivated = isActivated;
		}
	}
}
