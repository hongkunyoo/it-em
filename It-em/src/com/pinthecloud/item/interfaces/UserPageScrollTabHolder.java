package com.pinthecloud.item.interfaces;

import android.support.v7.widget.RecyclerView;

public interface UserPageScrollTabHolder {
	// Item to ItUserPage
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition);
	public void updateTabNumber(int position, int number);
	
	// ItUserPage to Item
	public void adjustScroll(int scrollHeight);
	public void updateHeader(int headerHeight);
}
