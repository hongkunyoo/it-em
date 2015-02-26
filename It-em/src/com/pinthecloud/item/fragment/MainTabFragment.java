package com.pinthecloud.item.fragment;

import com.pinthecloud.item.interfaces.MainTabHolder;

public abstract class MainTabFragment extends ItFragment implements MainTabHolder {

	protected MainTabHolder mTabHolder;

	public void setTabHolder(MainTabHolder tabHolder) {
		mTabHolder = tabHolder;
	}
	
	@Override
	public void updateNotiTab() {
	}
}
