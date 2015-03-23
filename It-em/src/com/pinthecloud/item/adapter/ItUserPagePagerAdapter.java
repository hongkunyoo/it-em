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
	private ItUser mItUser;
	private int mHeaderHeight;
	private int mTabHeight;

	private SparseArrayCompat<ItUserPageScrollTabHolder> mScrollTabHolderList;
	private ItUserPageScrollTabHolder mScrollTabHolder;

	public void setScrollTabHolder(ItUserPageScrollTabHolder ScrollTabHolder) {
		this.mScrollTabHolder = ScrollTabHolder;
	}
	public SparseArrayCompat<ItUserPageScrollTabHolder> getScrollTabHolderList() {
		return mScrollTabHolderList;
	}

	public ItUserPagePagerAdapter(FragmentManager fm, Context context, ItUser itUser, int headerHeight, int tabHeight) {
		super(fm);
		this.mItUser = itUser;
		this.mHeaderHeight = headerHeight;
		this.mTabHeight = tabHeight;
		this.mScrollTabHolderList = new SparseArrayCompat<ItUserPageScrollTabHolder>();
		
		this.mTitles = mItUser.checkPro() ?
				context.getResources().getStringArray(R.array.it_user_page_tab_pro_title_array) :
					context.getResources().getStringArray(R.array.it_user_page_tab_title_array);
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
		mScrollTabHolderList.put(position, fragment);
		if (mScrollTabHolder != null){
			fragment.setScrollTabHolder(mScrollTabHolder);
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
