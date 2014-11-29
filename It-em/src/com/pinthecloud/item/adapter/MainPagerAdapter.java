package com.pinthecloud.item.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeFragment;
import com.pinthecloud.item.fragment.HotFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.MyPageFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.IconTabProvider;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {

	private String[] mTitles;
	private int[] mTitleIcons = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};


	public MainPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mTitles = context.getResources().getStringArray(R.array.main_tab_title_string_array);
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.mTitles[position];
	}


	@Override
	public int getPageIconResId(int position) {
		return this.mTitleIcons[position];
	}


	@Override
	public Fragment getItem(int position) {
		ItFragment fragment = null;
		switch(position){
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new HotFragment();
			break;
		case 2:
			ItUser itUser = ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class);
			fragment = MyPageFragment.newInstance(itUser.getId());
			break;
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return this.mTitleIcons.length;
	}
}
