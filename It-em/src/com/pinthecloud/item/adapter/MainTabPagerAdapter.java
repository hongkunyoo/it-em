package com.pinthecloud.item.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItUserPageFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.IconTabProvider;

public class MainTabPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {

	private ItApplication mApp;
	private String[] mTitles;
	private int[] mTitleIcons = {R.drawable.launcher, R.drawable.launcher};


	public MainTabPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mApp = ItApplication.getInstance();
		this.mTitles = context.getResources().getStringArray(R.array.main_tab_title_array);
	}


	@Override
	public int getPageIconResId(int position) {
		return mTitleIcons[position];
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.mTitles[position];
	}
	
	
	@Override
	public Fragment getItem(int position) {
		ItFragment fragment = null;
		switch(position){
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			ItUser myItUser = mApp.getObjectPrefHelper().get(ItUser.class);
			fragment = ItUserPageFragment.newInstance(myItUser.getId());
			break;
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return this.mTitles.length;
	}
}
