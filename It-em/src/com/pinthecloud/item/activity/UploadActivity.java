package com.pinthecloud.item.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.R;
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
	}


	private void setFragment(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		UploadFragment fragment = new UploadFragment();
		fragmentTransaction.add(R.id.activity_container, fragment);
		fragmentTransaction.commit();
	}
}
