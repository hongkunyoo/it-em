package com.pinthecloud.item.activity;

import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItemFragment;
import com.pinthecloud.item.model.Item;

public class ItemActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	private Item mItem;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_toolbar_frame);

		mItem = getIntent().getParcelableExtra(Item.INTENT_KEY);
		setToolbar();
		setFragment(ItemFragment.newInstance(mItem));
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
		
		String title = String.format(Locale.US, getResources().getString(R.string.of_item), mItem.getWhoMade());
		actionBar.setTitle(title);
		
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mToolbarLayout.bringToFront();
	}
}
