package com.pinthecloud.item.interfaces;

import android.widget.AbsListView;

public interface ScrollTabHolder {
	public void adjustScroll(int scrollHeight);
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);
}
