package com.pinthecloud.item.view;

import com.pinthecloud.item.util.ItLog;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class DynamicHeightViewPager extends ViewPager {

	private double mHeightRatio;

	public DynamicHeightViewPager(Context context) {
		super(context);
	}
	
	public DynamicHeightViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setHeightRatio(double ratio) {
		if (ratio != mHeightRatio) {
			mHeightRatio = ratio;
			requestLayout();
		}
	}

	public double getHeightRatio() {
		return mHeightRatio;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		ItLog.log("onMeasure");
		
		if (mHeightRatio > 0.0) {
			// set the image views size
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) (width * mHeightRatio);
			setMeasuredDimension(width, height);
		}
		else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
