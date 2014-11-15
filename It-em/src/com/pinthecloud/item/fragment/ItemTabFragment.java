package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ItemPagerAdapter;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class ItemTabFragment extends ItFragment {

	private ViewPager viewPager;
	private ItemPagerAdapter itemPagerAdapter;
	private PagerSlidingTabStrip tab;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item_tab, container, false);
		findComponent(view);
		setTab();
		return view;
	}


	private void findComponent(View view){
		viewPager = (ViewPager) view.findViewById(R.id.item_tab_frag_pager);
		tab = (PagerSlidingTabStrip) view.findViewById(R.id.item_tab_frag_tab);
	}


	private void setTab(){
		itemPagerAdapter = new ItemPagerAdapter(getFragmentManager(), activity);
		viewPager.setAdapter(itemPagerAdapter);

		tab.setViewPager(viewPager);
		tab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
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
