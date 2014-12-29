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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolbar_frame);
		setToolbar();
		setFragment();
	}

	
	private void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		View shadow = findViewById(R.id.toolbar_shadow);
		shadow.bringToFront();
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = ItUserPageFragment.newInstance(getIntent().getStringExtra(ItUser.INTENT_KEY));
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
