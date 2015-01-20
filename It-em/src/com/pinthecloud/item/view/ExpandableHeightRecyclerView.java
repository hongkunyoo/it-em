package com.pinthecloud.item.view;

import com.pinthecloud.item.util.ItLog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ExpandableHeightRecyclerView extends RecyclerView {

	private boolean expanded = false;

	public ExpandableHeightRecyclerView(Context context){
		super(context);
	}

	public ExpandableHeightRecyclerView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public ExpandableHeightRecyclerView(Context context, AttributeSet attrs,
			int defStyle){
		super(context, attrs, defStyle);
	}

	public boolean isExpanded(){
		return expanded;
	}

	public void setExpanded(boolean expanded){
		this.expanded = expanded;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		// HACK! TAKE THAT ANDROID!
		if (isExpanded()){
			// Calculate entire height by providing a very large height hint.
			// But do not use the highest 2 bits of this integer; those are
			// reserved for the MeasureSpec mode.
			int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			getLayoutParams().height = getMeasuredHeight();
			requestLayout();
			
			ItLog.log("onMeasure");
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
