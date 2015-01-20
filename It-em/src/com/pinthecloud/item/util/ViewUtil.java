package com.pinthecloud.item.util;

import android.support.v7.widget.RecyclerView;
import android.view.View.MeasureSpec;

public class ViewUtil {

	@SuppressWarnings("unchecked")
	public static void setListHeightBasedOnChildren(RecyclerView recyclerView, int rowCount) {
		@SuppressWarnings("rawtypes")
		RecyclerView.Adapter adapter = recyclerView.getAdapter();
		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), MeasureSpec.AT_MOST);
		for (int i=0 ; i<rowCount ; i++) {
			RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(i));
			adapter.onBindViewHolder(holder, i);

			holder.itemView.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += holder.itemView.getMeasuredHeight();
		}
		recyclerView.getLayoutParams().height = totalHeight;
		recyclerView.requestLayout();
	}
}
