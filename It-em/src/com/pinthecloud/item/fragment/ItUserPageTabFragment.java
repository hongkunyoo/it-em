package com.pinthecloud.item.fragment;

import android.support.v7.widget.RecyclerView;

import com.pinthecloud.item.interfaces.ItUserPageTabHolder;
import com.pinthecloud.item.model.ItUser;

public abstract class ItUserPageTabFragment extends ItFragment implements ItUserPageTabHolder {

	protected static final String POSITION_KEY = "POSITION_KEY";
	protected int mPosition;
	protected ItUser mItUser;

	protected ItUserPageTabHolder mItUserPageTabHolder;
	public void setMyPageTabHolder(ItUserPageTabHolder itUserPageTabHolder) {
		mItUserPageTabHolder = itUserPageTabHolder;
	}

	@Override
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
	}
	@Override
	public void updateTabNumber(int position, int number) {
	}
}