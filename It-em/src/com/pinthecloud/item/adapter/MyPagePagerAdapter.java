package com.pinthecloud.item.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.fragment.ItItemFragment;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.fragment.MyPageItemFragment;
import com.pinthecloud.item.interfaces.ScrollTabHolder;
import com.pinthecloud.item.model.ItUser;

public class MyPagePagerAdapter extends FragmentStatePagerAdapter {

	public enum MY_PAGE_ITEM {
		MY_ITEM, IT_ITEM
	};

	private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
	private ScrollTabHolder mListener;
	private ItUser mItUser;


	public MyPagePagerAdapter(FragmentManager fm, Context context, ItUser itUser) {
		super(fm);
		this.mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
		this.mItUser = itUser;
	}

	
	public void setTabHolderScrollingContent(ScrollTabHolder listener) {
		mListener = listener;
	}
	public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
		return mScrollTabHolders;
	}
	
	
	@Override
	public Fragment getItem(int position) {
		MyPageItemFragment fragment = null;
		if(position == MY_PAGE_ITEM.MY_ITEM.ordinal()){
			fragment = (MyPageItemFragment) MyItemFragment.newInstance(position, mItUser);
		}else if(position == MY_PAGE_ITEM.IT_ITEM.ordinal()){
			fragment = (MyPageItemFragment) ItItemFragment.newInstance(position, mItUser);
		}
		
		mScrollTabHolders.put(position, fragment);
		if (mListener != null) fragment.setScrollTabHolder(mListener);
		return fragment;
	}


	@Override
	public int getCount() {
		return MY_PAGE_ITEM.values().length;
	}
}
