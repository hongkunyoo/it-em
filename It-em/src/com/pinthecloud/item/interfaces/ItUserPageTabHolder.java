package com.pinthecloud.item.interfaces;

import android.support.v7.widget.RecyclerView;

public interface ItUserPageTabHolder {
	// Item to MyPage
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition);
	public void updateTabNumber(int position, int number);
	
	// MyPage to Item
	public void adjustScroll(int scrollHeight);
	public void updateTab();
}
