package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItUserPageFragment;
import com.pinthecloud.item.model.ItUser;

public class ItUserPageActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_toolbar_frame);
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
		
		mToolbarLayout.bringToFront();
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = ItUserPageFragment.newInstance(getIntent().getStringExtra(ItUser.INTENT_KEY));
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
