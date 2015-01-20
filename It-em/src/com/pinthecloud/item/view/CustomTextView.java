package com.pinthecloud.item.view;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.pinthecloud.item.R;

public class CustomTextView extends TextView {

	private enum TEXT_TYPE{
		TITLE,
		SUBHEAD,
		BODY,
		CAPTION,
		BUTTON
	}

	private int mTextType = TEXT_TYPE.TITLE.ordinal();

	public CustomTextView(Context context) {
		this(context, null);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
		mTextType = a.getInt(R.styleable.CustomTextView_textType, 0);
		a.recycle();

		if(mTextType == TEXT_TYPE.TITLE.ordinal()){
			setTitle();
		} else if(mTextType == TEXT_TYPE.SUBHEAD.ordinal()) {
			setSubhead();
		} else if(mTextType == TEXT_TYPE.BODY.ordinal()) {
			setBody();
		} else if(mTextType == TEXT_TYPE.CAPTION.ordinal()) {
			setCaption();
		} else if(mTextType == TEXT_TYPE.BUTTON.ordinal()) {
			setButton();
		}
	}

	private void setTitle(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_x_large));
	}

	private void setSubhead(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_large));
	}

	private void setBody(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
	}

	private void setCaption(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_small));
	}

	private void setButton(){
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
		setText(getText().toString().toUpperCase(Locale.US));
	}


	@Override
	public void setText(CharSequence text, BufferType type) {
		if(mTextType == TEXT_TYPE.BUTTON.ordinal()){
			text = text.toString().toUpperCase(Locale.US);
		}
		super.setText(text, type);
	}
}
