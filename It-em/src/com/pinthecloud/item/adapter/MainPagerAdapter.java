package com.pinthecloud.item.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeFragment;
import com.pinthecloud.item.fragment.MyPageFragment;
import com.pinthecloud.item.fragment.HotFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

	private HomeFragment itemAllFragment;
	private HotFragment itemRankFragment;
	private MyPageFragment itemMyFragment;
	private String[] titles;


	public MainPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		itemAllFragment = new HomeFragment();
		itemRankFragment = new HotFragment();
		itemMyFragment = new MyPageFragment();
		titles = context.getResources().getStringArray(R.array.main_tab_title_string_array);
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position];
	}


	@Override
	public Fragment getItem(int position) {
		switch(position){
		case 0:
			return this.itemAllFragment;
		case 1:
			return this.itemRankFragment;
		case 2:
			return this.itemMyFragment;
		}
		return null;
	}


	@Override
	public int getCount() {
		return this.titles.length;
	}
}
