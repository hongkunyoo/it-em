package com.pinthecloud.item.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.pinthecloud.item.R;

public class ItSpan extends ClickableSpan {

	private Context mContext;
	private char mType;

	public ItSpan(Context context, char type) {
		super();
		this.mContext = context;
		this.mType = type;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		switch(mType){
		case '#':
			ds.setColor(mContext.getResources().getColor(R.color.accent_color));
			break;
		}
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
