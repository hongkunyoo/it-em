package com.pinthecloud.item.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.UploadFragment;
import com.pinthecloud.item.model.Item;

public class UploadActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	private Uri mItemImageUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_toolbar_frame);
		
		mItemImageUri = getIntent().getParcelableExtra(Item.INTENT_KEY);
		setToolbar();
		setFragment();
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_right);
	}
	
	
	@Override
	public View getToolbarLayout() {
		return mToolbarLayout;
	}
	
	
	private void setToolbar(){
		mToolbarLayout = findViewById(R.id.toolbar_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.appbar_close_ic);
		
		mToolbarLayout.bringToFront();
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = UploadFragment.newInstance(mItemImageUri);
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
