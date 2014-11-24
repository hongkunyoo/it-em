package com.pinthecloud.item.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.CollectItemFragment;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.fragment.MyPageItemFragment;
import com.pinthecloud.item.interfaces.ScrollTabHolder;

public class MyPagePagerAdapter extends FragmentStatePagerAdapter {

	private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
	private ScrollTabHolder mListener;
	private String[] mTitles;


	public MyPagePagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
		mTitles = context.getResources().getStringArray(R.array.my_page_tab_title_string_array);
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.mTitles[position];
	}


	@Override
	public Fragment getItem(int position) {
		MyPageItemFragment fragment = null;
		switch(position){
		case 0:
			fragment = (MyPageItemFragment) MyItemFragment.newInstance(position);
			break;
		case 1:
			fragment = (MyPageItemFragment) CollectItemFragment.newInstance(position);
			break;
		}
		mScrollTabHolders.put(position, fragment);
		if (mListener != null) {
			fragment.setScrollTabHolder(mListener);
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return this.mTitles.length;
	}


	public void setTabHolderScrollingContent(ScrollTabHolder listener) {
		mListener = listener;
	}


	public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
		return mScrollTabHolders;
	}
}
