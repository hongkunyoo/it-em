package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItemFragment;
import com.pinthecloud.item.model.Item;

public class ItemActivity extends ItActivity {

	private Item mItem;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolbar_frame);

		mItem = getIntent().getParcelableExtra(Item.INTENT_KEY);
		setToolbar();
		setFragment();
	}


	private void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		View shadow = findViewById(R.id.toolbar_shadow);
		shadow.bringToFront();

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mItem.getWhoMade() + getResources().getString(R.string.of) 
				+ " " + getResources().getString(R.string.app_name));
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = ItemFragment.newInstance(mItem);
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
