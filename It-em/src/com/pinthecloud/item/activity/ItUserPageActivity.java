package com.pinthecloud.item.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItUserPageFragment;

public class ItUserPageActivity extends ItActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolbar_frame);
		setToolbar();
		setFragment();
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setFragment();
	}


	private void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);
	}


	private void setFragment(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		ItFragment fragment = new ItUserPageFragment();
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
