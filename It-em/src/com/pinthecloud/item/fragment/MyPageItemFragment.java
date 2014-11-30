package com.pinthecloud.item.fragment;

import android.support.v7.widget.RecyclerView;

import com.pinthecloud.item.interfaces.MyPageTabHolder;
import com.pinthecloud.item.model.ItUser;

public abstract class MyPageItemFragment extends ItFragment implements MyPageTabHolder {

	protected static final String POSITION_KEY = "POSITION_KEY";
	protected int mPosition;
	protected ItUser mItUser;

	protected MyPageTabHolder mMyPageTabHolder;
	public void setMyPageTabHolder(MyPageTabHolder myPageTabHolder) {
		mMyPageTabHolder = myPageTabHolder;
	}

	@Override
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
	}
	@Override
	public void updateTabNumber(int position, int number) {
	}
}