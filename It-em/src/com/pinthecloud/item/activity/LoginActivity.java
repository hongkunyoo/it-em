package com.pinthecloud.item.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.LoginFragment;

public class LoginActivity extends ItActivity {

	private ItFragment fragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setFragment();
	}


	private void setFragment(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		fragment = new LoginFragment();
		transaction.add(R.id.activity_container, fragment);
		transaction.commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		fragment.onActivityResult(requestCode, resultCode, data);
	}
}
