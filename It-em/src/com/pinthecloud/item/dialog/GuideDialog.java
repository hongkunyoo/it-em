package com.pinthecloud.item.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;

public class GuideDialog extends ItDialogFragment {

	private ImageButton mClose;
	private TextView mBePro;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_guide, container, false);
		findComponent(view);
		setButton();
		return view;
	}


	private void findComponent(View view){
		mClose = (ImageButton)view.findViewById(R.id.guide_frag_close);
		mBePro = (TextView)view.findViewById(R.id.guide_frag_be_pro);
	}


	private void setButton(){
		mClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPrefHelper.put(ItConstant.GUIDE_READ_KEY, true);
				dismiss();
			}
		});

		mBePro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String homepage = "http://" + getResources().getString(R.string.homepage);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
				startActivity(intent);
			}
		});
	}
}
