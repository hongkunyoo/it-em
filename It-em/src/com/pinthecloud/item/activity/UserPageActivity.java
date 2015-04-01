package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.UserPageFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.ViewUtil;

public class UserPageActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_toolbar_frame);

		String userId = getIntent().getStringExtra(ItUser.INTENT_KEY);
		setToolbar();
		setContainer();
		setFragment(UserPageFragment.newInstance(userId));
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


	private void setContainer(){
		View container = findViewById(R.id.activity_container);
		container.setPadding(0, ViewUtil.getActionBarHeight(mThisActivity), 0, 0);
	}
}
