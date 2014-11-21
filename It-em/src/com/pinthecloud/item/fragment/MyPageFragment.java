package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyPagePagerAdapter;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MyPageFragment extends ItFragment {

	private TextView nickNameText;

	private PagerSlidingTabStrip tab;
	private ViewPager viewPager;
	private MyPagePagerAdapter myPagePagerAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_page, container, false);
		findComponent(view);
		setComponent();
		setTab();
		return view;
	}
	
	
	private void findComponent(View view){
		nickNameText = (TextView)view.findViewById(R.id.my_page_frag_nick_name);
		tab = (PagerSlidingTabStrip) view.findViewById(R.id.my_page_frag_tab);
		viewPager = (ViewPager)view.findViewById(R.id.my_page_frag_pager);
	}


	private void setComponent(){
	}


	private void setTab(){
		myPagePagerAdapter = new MyPagePagerAdapter(getFragmentManager(), activity);
		viewPager.setAdapter(myPagePagerAdapter);
		tab.setViewPager(viewPager);
	}
}
