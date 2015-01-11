package com.pinthecloud.item.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ProfileSettingsFragment;
import com.pinthecloud.item.model.ItUser;

public class ProfileSettingsActivity extends ItActivity {

	private Toolbar mToolbar;
	private Bitmap mProfileImage;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolbar_light_frame);

		mProfileImage = getIntent().getParcelableExtra(ItUser.INTENT_KEY_IMAGE);
		setToolbar();
		setFragment();
	}


	@Override
	public Toolbar getToolbar() {
		return mToolbar;
	}


	private void setToolbar(){
		mToolbar = (Toolbar) findViewById(R.id.toolbar_light);
		setSupportActionBar(mToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.appbar_close_ic);
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = ProfileSettingsFragment.newInstance(mProfileImage);
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
