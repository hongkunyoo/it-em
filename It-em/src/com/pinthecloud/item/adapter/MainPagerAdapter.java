package com.pinthecloud.item.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HomeFragment;
import com.pinthecloud.item.fragment.UserPageFragment;
import com.pinthecloud.item.fragment.MainTabFragment;
import com.pinthecloud.item.fragment.NotiFragment;
import com.pinthecloud.item.interfaces.MainTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.PagerSlidingTabStrip.CustomTabProvider;

public class MainPagerAdapter extends FragmentStatePagerAdapter implements CustomTabProvider {

	public static enum TAB{
		HOME,
		NOTI,
		IT_USER_PAGE
	}

	private ItApplication mApp;
	private int[] mTitleIcons = {R.drawable.main_tab_home, R.drawable.main_tab_noti, R.drawable.main_tab_it_user_page};

	private MainTabHolder mTabHolder;
	private SparseArrayCompat<MainTabHolder> mTabHolderList;

	public void setTabHolder(MainTabHolder tabHolder) {
		this.mTabHolder = tabHolder;
	}
	public SparseArrayCompat<MainTabHolder> getTabHolderList() {
		return mTabHolderList;
	}
	
	public MainPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mApp = ItApplication.getInstance();
		this.mTabHolderList = new SparseArrayCompat<MainTabHolder>();
	}

	@Override
	public int getPageLayoutResId(int position) {
		return R.layout.tab_main;
	}

	@Override
	public Fragment getItem(int position) {
		MainTabFragment fragment = null;
		if(position == TAB.HOME.ordinal()){
			fragment = new HomeFragment();
		} else if(position == TAB.NOTI.ordinal()){
			fragment = new NotiFragment();
		} else if(position == TAB.IT_USER_PAGE.ordinal()){
			ItUser user = mApp.getObjectPrefHelper().get(ItUser.class);
			fragment = UserPageFragment.newInstance(user.getId());
		}
		
		mTabHolderList.put(position, fragment);
		if (mTabHolder != null){
			fragment.setTabHolder(mTabHolder);
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
