package com.pinthecloud.item.fragment;

import android.widget.AbsListView;

import com.pinthecloud.item.interfaces.ScrollTabHolder;

public abstract class MyPageItemFragment extends ItFragment implements ScrollTabHolder {

	protected static final String POSITION_KEY = "POSITION_KEY";
	protected int mPosition;
	protected int mFirstVisibleItem = 0;
	protected boolean mIsAdding = false;
	protected ScrollTabHolder mScrollTabHolder;

	public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
	}
}