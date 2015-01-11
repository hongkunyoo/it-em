package com.pinthecloud.item.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ProfileImageFragment;
import com.pinthecloud.item.model.ItUser;

public class ProfileImageActivity extends ItActivity {

	private String mItUserId;
	private Bitmap mProfileImage;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		
		mItUserId = getIntent().getStringExtra(ItUser.INTENT_KEY);
		mProfileImage = getIntent().getParcelableExtra(ItUser.INTENT_KEY_IMAGE);
		setFragment();
	}


	@Override
	public Toolbar getToolbar() {
		return null;
	}
	
	
	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = ProfileImageFragment.newInstance(mItUserId, mProfileImage);
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
