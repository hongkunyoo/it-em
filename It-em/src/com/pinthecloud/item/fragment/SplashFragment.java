package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.HongkunTestActivity;
import com.pinthecloud.item.activity.LoginActivity;

public class SplashFragment extends ItFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_splash, container, false);
		runItem();
		return view;
	}


	private void runItem() {
		goToNextActivity();
	}


	private void goToNextActivity() {
		boolean isHong = false;
		try {
			Class.forName("com.pinthecloud.item.util.HongUtil");
			isHong = true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			isHong = false;
		}
		if (isHong) {
			Intent hongTent = new Intent();
			hongTent.setClass(mActivity, HongkunTestActivity.class);
			startActivity(hongTent);
			return;
		}
		
		if(mThisFragment.isAdded()){
			Intent intent = new Intent();
			intent.setClass(mActivity, LoginActivity.class);
			startActivity(intent);
		}
	}
}
