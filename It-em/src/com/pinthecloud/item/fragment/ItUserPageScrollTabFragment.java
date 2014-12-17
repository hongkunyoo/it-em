package com.pinthecloud.item.fragment;

import android.support.v7.widget.RecyclerView;

import com.pinthecloud.item.interfaces.ItUserPageScrollTabHolder;
import com.pinthecloud.item.model.ItUser;

public abstract class ItUserPageScrollTabFragment extends ItFragment implements ItUserPageScrollTabHolder {

	protected static final String POSITION_KEY = "POSITION_KEY";
	protected int mPosition;
	protected ItUser mItUser;

	protected ItUserPageScrollTabHolder mItUserPageScrollTabHolder;
	public void setItUserPageScrollTabHolder(ItUserPageScrollTabHolder itUserPageScrollTabHolder) {
		mItUserPageScrollTabHolder = itUserPageScrollTabHolder;
	}

	@Override
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
	}
	@Override
	public void updateTabNumber(int position, int number) {
	}
}