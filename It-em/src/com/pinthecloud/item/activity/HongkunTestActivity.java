package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HongkunTestFragment;
import com.pinthecloud.item.fragment.ItFragment;

public class HongkunTestActivity extends ItActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setFragment();
	}
	
	
	@Override
	public View getToolbarLayout() {
		return null;
	}
	
	
	private void setFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ItFragment fragment = new HongkunTestFragment();
		fragmentTransaction.add(R.id.activity_container, fragment);
		fragmentTransaction.commit();
	}
}
