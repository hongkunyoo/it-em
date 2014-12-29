package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.UploadFragment;

public class UploadActivity extends ItActivity {

	private Toolbar mToolbar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolbar_light_frame);
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
		ItFragment fragment = new UploadFragment();
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
