package com.pinthecloud.item.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.MyItemFragment;
import com.pinthecloud.item.interfaces.UserPageScrollTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.CustomTabProvider;

public class UserPagePagerAdapter extends FragmentStatePagerAdapter implements CustomTabProvider {

	private String[] mTitles;
	private ItUser mUser;
	private int mHeaderHeight;
	private int mTabHeight;

	private SparseArrayCompat<UserPageScrollTabHolder> mScrollTabHolderList;
	private UserPageScrollTabHolder mScrollTabHolder;

	public void setScrollTabHolder(UserPageScrollTabHolder ScrollTabHolder) {
		this.mScrollTabHolder = ScrollTabHolder;
	}
	public SparseArrayCompat<UserPageScrollTabHolder> getScrollTabHolderList() {
		return mScrollTabHolderList;
	}

	public UserPagePagerAdapter(FragmentManager fm, Context context, ItUser user, int headerHeight, int tabHeight) {
		super(fm);
		this.mUser = user;
		this.mHeaderHeight = headerHeight;
		this.mTabHeight = tabHeight;
		this.mScrollTabHolderList = new SparseArrayCompat<UserPageScrollTabHolder>();
		
		this.mTitles = mUser.checkPro() ?
				context.getResources().getStringArray(R.array.user_page_tab_pro_title_array) :
					context.getResources().getStringArray(R.array.user_page_tab_title_array);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return this.mTitles[position];
	}

	@Override
	public int getPageLayoutResId(int position) {
		return R.layout.tab_user_page;
	}

	@Override
	public Fragment getItem(int position) {
		MyItemFragment fragment = MyItemFragment.newInstance(position, mUser, mHeaderHeight, mTabHeight);
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
