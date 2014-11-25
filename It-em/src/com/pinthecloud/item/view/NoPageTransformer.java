package com.pinthecloud.item.view;

import android.support.v4.view.ViewPager;
import android.view.View;

public class NoPageTransformer implements ViewPager.PageTransformer {

	@Override
	public void transformPage(View view, float position) {
        if (position < 0) {
            view.setScrollX((int)((float)(view.getWidth()) * position));
        } else if (position > 0) {
            view.setScrollX(-(int) ((float) (view.getWidth()) * -position));
        } else {
            view.setScrollX(0);
        }
    }
}
