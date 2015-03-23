package com.pinthecloud.item.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.pinthecloud.item.R;

public class HashTagSpan extends ClickableSpan {

	private Context mContext;

	public HashTagSpan(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(mContext.getResources().getColor(R.color.accent_color));
	}

	@Override
	public void onClick(View widget) {
		//        TextView tv = (TextView) widget;
		//        Spanned s = (Spanned) tv.getText();
		//        int start = s.getSpanStart(this);
		//        int end = s.getSpanEnd(this);
		//        String theWord = s.subSequence(start + 1, end).toString();
	}
}
