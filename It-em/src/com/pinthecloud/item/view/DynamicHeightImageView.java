package com.pinthecloud.item.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DynamicHeightImageView extends ImageView {

	private double mHeightRatio;

	public DynamicHeightImageView(Context context) {
		super(context);
	}
	
	public DynamicHeightImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DynamicHeightImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
