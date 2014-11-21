package com.pinthecloud.item.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeFragment;
import com.pinthecloud.item.fragment.HotFragment;
import com.pinthecloud.item.fragment.MyPageFragment;
import com.pinthecloud.item.view.PagerSlidingTabStrip.IconTabProvider;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {

	private String[] titles;
	private int[] titleIcons = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
	private HomeFragment homeFragment;
	private HotFragment hotFragment;
	private MyPageFragment myPageFragment;


	public MainPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		homeFragment = new HomeFragment();
		hotFragment = new HotFragment();
		myPageFragment = new MyPageFragment();
		titles = context.getResources().getStringArray(R.array.main_tab_title_string_array);
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position];
	}


	@Override
	public int getPageIconResId(int position) {
		return this.titleIcons[position];
	}


	@Override
	public Fragment getItem(int position) {
		switch(position){
		case 0:
			return this.homeFragment;
		case 1:
			return this.hotFragment;
		case 2:
			return this.myPageFragment;
		}
		return null;
	}


	@Override
	public int getCount() {
		return this.titleIcons.length;
	}
}
