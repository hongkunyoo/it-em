package com.pinthecloud.item.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItUserPageFragment;
import com.pinthecloud.item.fragment.NotificationFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.IconTabProvider;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {

	private ItApplication mApp;
	private int[] mTitleIcons = {R.drawable.launcher, R.drawable.launcher, R.drawable.launcher};


	public MainPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mApp = ItApplication.getInstance();
	}


	@Override
	public int getPageIconResId(int position) {
		return mTitleIcons[position];
	}

	
	@Override
	public Fragment getItem(int position) {
		ItFragment fragment = null;
		switch(position){
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new NotificationFragment();
			break;
		case 2:
			ItUser myItUser = mApp.getObjectPrefHelper().get(ItUser.class);
			fragment = ItUserPageFragment.newInstance(myItUser.getId());
			break;
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return mTitleIcons.length;
	}
	
	
	@Override
	public Parcelable saveState() {
		return null;
	}
}
