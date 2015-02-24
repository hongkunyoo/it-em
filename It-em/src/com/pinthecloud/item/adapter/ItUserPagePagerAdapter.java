package com.pinthecloud.item.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.interfaces.ItUserPageScrollTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.CustomTabProvider;

public class ItUserPagePagerAdapter extends FragmentStatePagerAdapter implements CustomTabProvider {

	private String[] mTitles;
	private SparseArrayCompat<ItUserPageScrollTabHolder> mItUserPageScrollTabHolderList;
	private ItUserPageScrollTabHolder mItUserPageScrollTabHolder;
	private ItUser mItUser;
	private int mHeaderHeight;
	private int mTabHeight;


	public ItUserPagePagerAdapter(FragmentManager fm, Context context, ItUser itUser, int headerHeight, int tabHeight) {
		super(fm);
		this.mItUserPageScrollTabHolderList = new SparseArrayCompat<ItUserPageScrollTabHolder>();
		this.mItUser = itUser;
		this.mHeaderHeight = headerHeight;
		this.mTabHeight = tabHeight;

		if(mItUser.checkPro()){
			this.mTitles = context.getResources().getStringArray(R.array.it_user_page_pro_tab_title_array);	
		} else {
			this.mTitles = context.getResources().getStringArray(R.array.it_user_page_tab_title_array);
		}
	}


	public void setItUserPageScrollTabHolder(ItUserPageScrollTabHolder itUserPageScrollTabHolder) {
		this.mItUserPageScrollTabHolder = itUserPageScrollTabHolder;
	}
	public SparseArrayCompat<ItUserPageScrollTabHolder> getItUserPageScrollTabHolderList() {
		return mItUserPageScrollTabHolderList;
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
		MyItemFragment fragment = MyItemFragment.newInstance(position, mItUser, mHeaderHeight, mTabHeight);
		mItUserPageScrollTabHolderList.put(position, fragment);
		if (mItUserPageScrollTabHolder != null){
			fragment.setItUserPageScrollTabHolder(mItUserPageScrollTabHolder);
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return this.mTitles.length;
	}


	@Override
	public Parcelable saveState() {
		return null;
	}
}
