package com.pinthecloud.item.util;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View.MeasureSpec;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;

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


	public static int getActionBarHeight(ItActivity activity){
		int actionBarHeight = activity.getSupportActionBar().getHeight();
		if (actionBarHeight != 0){
			return actionBarHeight;
		}

		final TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)){
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}
}
