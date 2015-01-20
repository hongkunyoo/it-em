package com.pinthecloud.item.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.pinthecloud.item.util.ViewUtil;

public class ExpandableHeightRecyclerView extends RecyclerView {

	private boolean isExpand = false;
	private int expandRowCount = 0;

	
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

	
	public void setOnDrawExpandRowCount(int expandRowCount) {
		this.isExpand = true;
		this.expandRowCount = expandRowCount;
	}

	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isExpand){
			ViewUtil.setListHeightBasedOnChildren(this, expandRowCount);
			isExpand = false;
		}
	}
}
