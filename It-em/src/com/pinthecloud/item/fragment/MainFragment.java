package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MainPagerAdapter;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.view.PagerSlidingTabStrip;
import com.pinthecloud.item.view.ParentViewPager;

public class MainFragment extends ItFragment {

	private PagerSlidingTabStrip mTab;
	private ParentViewPager mViewPager;
	private MainPagerAdapter mMainPagerAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		int startTab = mPrefHelper.getInt(PrefHelper.MAIN_EXIT_TAB);
		findComponent(view);
		setTab(startTab);
		setActionBar(startTab);

		return view;
	}


	private void setActionBar(int position){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(mMainPagerAdapter.getPageTitle(position));
	}


	private void findComponent(View view){
		mTab = (PagerSlidingTabStrip) view.findViewById(R.id.main_frag_tab);
		mViewPager = (ParentViewPager) view.findViewById(R.id.main_frag_pager);
	}


	private void setTab(int position){
		mMainPagerAdapter = new MainPagerAdapter(getFragmentManager(), mActivity);
		mViewPager.setOffscreenPageLimit(mMainPagerAdapter.getCount());
		mViewPager.setAdapter(mMainPagerAdapter);
		mViewPager.setCurrentItem(position);

		mTab.setStartTab(position);
		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mPrefHelper.put(PrefHelper.MAIN_EXIT_TAB, position);
				setActionBar(position);
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
}
