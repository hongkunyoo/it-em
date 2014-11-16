package com.pinthecloud.item.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItemAllFragment;
import com.pinthecloud.item.fragment.ItemMyFragment;
import com.pinthecloud.item.fragment.ItemRankFragment;

public class ItemPagerAdapter extends FragmentStatePagerAdapter {

	private ItemAllFragment itemAllFragment;
	private ItemRankFragment itemRankFragment;
	private ItemMyFragment itemMyFragment;
	private String[] titles;


	public ItemPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		itemAllFragment = new ItemAllFragment();
		itemRankFragment = new ItemRankFragment();
		itemMyFragment = new ItemMyFragment();
		titles = context.getResources().getStringArray(R.array.item_tab_title_string_array);
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position];
	}


	@Override
	public Fragment getItem(int position) {
		switch(position){
		case 0:
			return this.itemAllFragment;
		case 1:
			return this.itemRankFragment;
		case 2:
			return this.itemMyFragment;
		}
		return null;
	}


	@Override
	public int getCount() {
		return this.titles.length;
	}
}
