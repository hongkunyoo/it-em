package com.pinthecloud.item.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.UploadFragment;

public class UploadActivity extends ItActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolbar_light_frame);
		setToolbar();
		setFragment();
	}


	private void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		toolbar.setNavigationIcon(R.drawable.appbar_close_ic);
	}


	private void setFragment(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		ItFragment fragment = new UploadFragment();
		transaction.add(R.id.activity_container, fragment);
		transaction.commit();
	}
}
