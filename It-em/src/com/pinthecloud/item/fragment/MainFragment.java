package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MainPagerAdapter;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MainFragment extends ItFragment {

	private PagerSlidingTabStrip tab;
	private ViewPager viewPager;
	private MainPagerAdapter mainPagerAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		findComponent(view);
		setTab();
		return view;
	}


	private void findComponent(View view){
		tab = (PagerSlidingTabStrip) view.findViewById(R.id.main_frag_tab);
		viewPager = (ViewPager) view.findViewById(R.id.main_frag_pager);
	}


	private void setTab(){
		int startTab = prefHelper.getInt(PrefHelper.MAIN_EXIT_TAB);

		mainPagerAdapter = new MainPagerAdapter(getFragmentManager(), activity);
		viewPager.setAdapter(mainPagerAdapter);
		viewPager.setCurrentItem(startTab);

		tab.setViewPager(viewPager);
		tab.setStartTab(startTab);
		tab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				prefHelper.put(PrefHelper.MAIN_EXIT_TAB, position);
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
