package com.pinthecloud.item.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class DisallowChildScrollViewPager extends ViewPager {

	public DisallowChildScrollViewPager(Context context) {
		super(context);
	}

	public DisallowChildScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		return false;
	}
}
