package com.pinthecloud.item.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.LoginFragment;

public class LoginActivity extends ItActivity {

	private ItFragment mLoginFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_frame);

		mLoginFragment = new LoginFragment();
		setFragment(mLoginFragment);
	}


	@Override
	public View getToolbarLayout() {
		return null;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(mLoginFragment.isVisible()){
			mLoginFragment.onActivityResult(requestCode, resultCode, data);
		}
	}
}
