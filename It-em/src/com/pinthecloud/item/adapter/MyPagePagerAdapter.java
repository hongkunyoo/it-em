package com.pinthecloud.item.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.fragment.ItItemFragment;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.fragment.MyPageItemFragment;
import com.pinthecloud.item.interfaces.MyPageTabHolder;
import com.pinthecloud.item.model.ItUser;

public class MyPagePagerAdapter extends FragmentStatePagerAdapter {

	public enum MY_PAGE_ITEM {
		MY_ITEM, IT_ITEM
	};

	private SparseArrayCompat<MyPageTabHolder> mMyPageTabHolders;
	private MyPageTabHolder mMyPageTabHolder;
	private ItUser mItUser;


	public MyPagePagerAdapter(FragmentManager fm, Context context, ItUser itUser) {
		super(fm);
		this.mMyPageTabHolders = new SparseArrayCompat<MyPageTabHolder>();
		this.mItUser = itUser;
	}


	public void setMyPageTabHolder(MyPageTabHolder myPageTabHolder) {
		this.mMyPageTabHolder = myPageTabHolder;
	}
	public SparseArrayCompat<MyPageTabHolder> getMyPageTabHolders() {
		return mMyPageTabHolders;
	}


	@Override
	public Fragment getItem(int position) {
		MyPageItemFragment fragment = null;
		if(position == MY_PAGE_ITEM.MY_ITEM.ordinal()){
			fragment = (MyPageItemFragment) MyItemFragment.newInstance(position, mItUser);
		}else if(position == MY_PAGE_ITEM.IT_ITEM.ordinal()){
			fragment = (MyPageItemFragment) ItItemFragment.newInstance(position, mItUser);
		}

		mMyPageTabHolders.put(position, fragment);
		if (mMyPageTabHolder != null) fragment.setMyPageTabHolder(mMyPageTabHolder);
		return fragment;
	}


	@Override
	public int getCount() {
		return MY_PAGE_ITEM.values().length;
	}
}
