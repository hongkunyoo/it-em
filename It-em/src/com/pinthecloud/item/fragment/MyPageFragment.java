package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.SettingsActivity;
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.MyPagePagerAdapter;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MyPageFragment extends ItFragment {

	private TextView nickNameText;
	private Button uploadButton;

	private PagerSlidingTabStrip tab;
	private ViewPager viewPager;
	private MyPagePagerAdapter myPagePagerAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_page, container, false);
		setHasOptionsMenu(true);
		findComponent(view);
		setComponent();
		setButton();
		setTab();
		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.my_page, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.my_page_settings:
			Intent intent = new Intent(activity, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		nickNameText = (TextView)view.findViewById(R.id.my_page_frag_nick_name);
		uploadButton = (Button)view.findViewById(R.id.my_page_frag_upload_button);
		tab = (PagerSlidingTabStrip) view.findViewById(R.id.my_page_frag_tab);
		viewPager = (ViewPager)view.findViewById(R.id.my_page_frag_pager);
	}


	private void setComponent(){
	}


	private void setButton(){
		uploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, UploadActivity.class);
				startActivity(intent);
			}
		});
	}


	private void setTab(){
		myPagePagerAdapter = new MyPagePagerAdapter(getFragmentManager(), activity);
		viewPager.setAdapter(myPagePagerAdapter);
		tab.setViewPager(viewPager);
	}
}
