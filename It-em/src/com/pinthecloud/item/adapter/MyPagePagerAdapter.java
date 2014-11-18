package com.pinthecloud.item.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.CollectItemFragment;
import com.pinthecloud.item.fragment.MyItemFragment;

public class MyPagePagerAdapter extends FragmentStatePagerAdapter {

	private MyItemFragment myItemFragment;
	private CollectItemFragment collectItemFragment;
	private String[] titles;


	public MyPagePagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		myItemFragment = new MyItemFragment();
		collectItemFragment = new CollectItemFragment();
		titles = context.getResources().getStringArray(R.array.my_page_tab_title_string_array);
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position];
	}


	@Override
	public Fragment getItem(int position) {
		switch(position){
		case 0:
			return this.myItemFragment;
		case 1:
			return this.collectItemFragment;
		}
		return null;
	}


	@Override
	public int getCount() {
		return this.titles.length;
	}

}
