package com.pinthecloud.item.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

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


	@Override
	public Toolbar getToolbar() {
		return null;
	}
	
	
	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		fragment = new LoginFragment();
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		fragment.onActivityResult(requestCode, resultCode, data);
	}
}
