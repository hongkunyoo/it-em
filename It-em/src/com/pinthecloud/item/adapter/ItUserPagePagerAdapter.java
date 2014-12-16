package com.pinthecloud.item.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItItemFragment;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.fragment.ItUserPageTabFragment;
import com.pinthecloud.item.interfaces.ItUserPageTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.CustomTabProvider;

public class ItUserPagePagerAdapter extends FragmentStatePagerAdapter implements CustomTabProvider {

	private String[] mTitles;
	private SparseArrayCompat<ItUserPageTabHolder> mItUserPageTabHolders;
	private ItUserPageTabHolder mItUserPageTabHolder;
	private ItUser mItUser;


	public ItUserPagePagerAdapter(FragmentManager fm, Context context, ItUser itUser) {
		super(fm);
		this.mTitles = context.getResources().getStringArray(R.array.it_user_page_tab_title_string_array);
		this.mItUserPageTabHolders = new SparseArrayCompat<ItUserPageTabHolder>();
		this.mItUser = itUser;
	}


	public void setItUserPageTabHolder(ItUserPageTabHolder itUserPageTabHolder) {
		this.mItUserPageTabHolder = itUserPageTabHolder;
	}
	public SparseArrayCompat<ItUserPageTabHolder> getItUserPageTabHolders() {
		return mItUserPageTabHolders;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.mTitles[position];
	}


	@Override
	public int getPageLayoutResId(int position) {
		return R.layout.tab_it_user_page;
	}


	@Override
	public Fragment getItem(int position) {
		ItUserPageTabFragment fragment = null;
		switch(position){
		case 0:
			fragment = (ItUserPageTabFragment) MyItemFragment.newInstance(position, mItUser);
			break;
		case 1:
			fragment = (ItUserPageTabFragment) ItItemFragment.newInstance(position, mItUser);
			break;
		}

		mItUserPageTabHolders.put(position, fragment);
		if (mItUserPageTabHolder != null) fragment.setItUserPageTabHolder(mItUserPageTabHolder);
		return fragment;
	}


	@Override
	public int getCount() {
		return this.mTitles.length;
	}
}
