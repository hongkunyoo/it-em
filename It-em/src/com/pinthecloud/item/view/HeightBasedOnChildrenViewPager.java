package com.pinthecloud.item.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class HeightBasedOnChildrenViewPager extends ViewPager {

	public HeightBasedOnChildrenViewPager(Context context) {
		super(context);
	}

	public HeightBasedOnChildrenViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		View view = getChildAt(this.getCurrentItem());
		if (view != null) {
			view.measure(widthMeasureSpec, heightMeasureSpec);
		}
		setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, view));
	}

	private int measureHeight(int measureSpec, View view) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			// set the height from the base view if available
			if (view != null) {
				result = view.getMeasuredHeight();
			}
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
}
