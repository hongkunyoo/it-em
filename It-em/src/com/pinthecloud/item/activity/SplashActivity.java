package com.pinthecloud.item.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.SplashFragment;

public class SplashActivity extends ItActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setFragment();
	}


	private void setFragment(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		ItFragment fragment = new SplashFragment();
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
}
