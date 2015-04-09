package com.pinthecloud.item.view;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.pinthecloud.item.R;

public class ItTextView extends TextView {

	public static enum TYPE{
		TITLE,
		SUBHEAD,
		BODY,
		CAPTION,
		BUTTON
	}

	private int mTextType = TYPE.TITLE.ordinal();

	public ItTextView(Context context) {
		this(context, null);
	}

	public ItTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ItTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItTextView);
		mTextType = a.getInt(R.styleable.ItTextView_textType, 0);
		a.recycle();

		setTextProperty(mTextType);
	}

	private void setTextProperty(int textType) {
		if(textType == TYPE.TITLE.ordinal()){
			setTitle();
		} else if(textType == TYPE.SUBHEAD.ordinal()) {
			setSubhead();
		} else if(textType == TYPE.BODY.ordinal()) {
			setBody();
		} else if(textType == TYPE.CAPTION.ordinal()) {
			setCaption();
		} else if(textType == TYPE.BUTTON.ordinal()) {
			setButton();
		}
	}

	private void setTitle(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_large));
	}

	private void setSubhead(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
	}

	private void setBody(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_small));
	}

	private void setCaption(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_micro));
	}

	private void setButton(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
		setText(getText().toString().toUpperCase(Locale.US));
	}
}
