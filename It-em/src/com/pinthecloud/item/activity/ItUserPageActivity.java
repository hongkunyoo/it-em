package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItUserPageFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.ViewUtil;

public class ItUserPageActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	private View mContainer;


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

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mToolbarLayout.bringToFront();
	}


	private void setFragment(){
		mContainer = findViewById(R.id.activity_container);
		mContainer.setPadding(0, ViewUtil.getActionBarHeight(mThisActivity), 0, 0);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		final ItFragment fragment = ItUserPageFragment.newInstance(getIntent().getStringExtra(ItUser.INTENT_KEY));
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
