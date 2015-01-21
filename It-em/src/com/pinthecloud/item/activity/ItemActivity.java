package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItemFragment;
import com.pinthecloud.item.model.Item;

public class ItemActivity extends ItActivity {

	private Item mItem;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);

		mItem = getIntent().getParcelableExtra(Item.INTENT_KEY);
		setFragment();
	}


	@Override
	public Toolbar getToolbar() {
		return null;
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = ItemFragment.newInstance(mItem);
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
