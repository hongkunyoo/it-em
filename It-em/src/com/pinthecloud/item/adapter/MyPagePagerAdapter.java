package com.pinthecloud.item.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItItemFragment;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.fragment.MyPageTabFragment;
import com.pinthecloud.item.interfaces.MyPageTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.CustomTabProvider;

public class MyPagePagerAdapter extends FragmentStatePagerAdapter implements CustomTabProvider {

	private String[] mTitles;
	private SparseArrayCompat<MyPageTabHolder> mMyPageTabHolders;
	private MyPageTabHolder mMyPageTabHolder;
	private ItUser mItUser;


	public MyPagePagerAdapter(FragmentManager fm, Context context, ItUser itUser) {
		super(fm);
		this.mTitles = context.getResources().getStringArray(R.array.my_page_tab_title_string_array);
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
	public CharSequence getPageTitle(int position) {
		return this.mTitles[position];
	}


	@Override
	public int getPageLayoutResId(int position) {
		return R.layout.tab_my_page;
	}


	@Override
	public Fragment getItem(int position) {
		MyPageTabFragment fragment = null;
		switch(position){
		case 0:
			fragment = (MyPageTabFragment) MyItemFragment.newInstance(position, mItUser);
			break;
		case 1:
			fragment = (MyPageTabFragment) ItItemFragment.newInstance(position, mItUser);
			break;
		}

		mMyPageTabHolders.put(position, fragment);
		if (mMyPageTabHolder != null) fragment.setMyPageTabHolder(mMyPageTabHolder);
		return fragment;
	}


	@Override
	public int getCount() {
		return this.mTitles.length;
	}
}
