package com.pinthecloud.item.fragment;

import android.support.v7.widget.RecyclerView;

import com.pinthecloud.item.interfaces.ScrollTabHolder;
import com.pinthecloud.item.model.ItUser;

public abstract class MyPageItemFragment extends ItFragment implements ScrollTabHolder {

	protected static final String POSITION_KEY = "POSITION_KEY";
	protected int mPosition;
	protected ItUser mItUser;

	protected ScrollTabHolder mScrollTabHolder;
	public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
	}
}