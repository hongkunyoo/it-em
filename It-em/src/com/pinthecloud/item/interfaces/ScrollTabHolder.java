package com.pinthecloud.item.interfaces;

import android.support.v7.widget.RecyclerView;

public interface ScrollTabHolder {
	public void adjustScroll(int scrollHeight);
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition);
	public void updateTabNumber(int position, int number);
}
