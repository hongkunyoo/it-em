package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MainPagerAdapter;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MainTabActivity extends ItActivity {

	private PagerSlidingTabStrip mTab;
	private ViewPager mViewPager;
	private MainPagerAdapter mViewPagerAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tab);
		findComponent();
		setViewPager();
		setTab();
	}
	
	
	@Override
	public View getToolbarLayout() {
		return null;
	}
	
	
	private void findComponent(){
		mTab = (PagerSlidingTabStrip)findViewById(R.id.main_tab);
		mViewPager = (ViewPager)findViewById(R.id.main_pager); 
	}
	
	
	private void setViewPager(){
		mViewPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);
	}


	private void setTab(){
		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageSelected(int position) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
}
