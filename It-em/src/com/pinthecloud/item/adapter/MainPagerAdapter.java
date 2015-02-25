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
import com.pinthecloud.item.fragment.NotiFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.CustomTabProvider;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements CustomTabProvider {

	public static enum TAB{
		HOME,
		NOTI,
		IT_USER_PAGE
	}


	private ItApplication mApp;
	private int[] mTitleIcons = {R.drawable.launcher, R.drawable.launcher, R.drawable.launcher};


	public MainPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mApp = ItApplication.getInstance();
	}


	@Override
	public int getPageLayoutResId(int position) {
		return R.layout.tab_main;
	}


	@Override
	public Fragment getItem(int position) {
		ItFragment fragment = null;
		if(position == TAB.HOME.ordinal()){
			fragment = new HomeFragment();
		} else if(position == TAB.NOTI.ordinal()){
			fragment = new NotiFragment();
		} else if(position == TAB.IT_USER_PAGE.ordinal()){
			ItUser myItUser = mApp.getObjectPrefHelper().get(ItUser.class);
			fragment = ItUserPageFragment.newInstance(myItUser.getId());
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


	public int getPageIconResId(int position) {
		return mTitleIcons[position];
	}
}
