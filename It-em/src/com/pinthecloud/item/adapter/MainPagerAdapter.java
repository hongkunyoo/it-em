package com.pinthecloud.item.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.view.PagerSlidingTabStrip.IconTabProvider;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {

	private String[] mTitles;
	private int[] mTitleIcons;
	
	
	public MainPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	
	@Override
	public int getPageIconResId(int position) {
		return 0;
	}

	
	@Override
	public Fragment getItem(int arg0) {
		return null;
	}

	
	@Override
	public int getCount() {
		return 0;
	}
}
