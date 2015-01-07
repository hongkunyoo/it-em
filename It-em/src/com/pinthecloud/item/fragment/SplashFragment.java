package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.HongkunTestActivity;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.model.ItUser;

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
		String className = "com.pinthecloud.item.util.HongUtil2";
		if (GlobalVariable.HONG_DEBUG_MODE) className = "com.pinthecloud.item.util.HongUtil";
		try {
			Class.forName(className);
			Intent hongTent = new Intent();
			hongTent.setClass(mActivity, HongkunTestActivity.class);
			startActivity(hongTent);
		} catch (ClassNotFoundException e) {
			if(isAdded()){
				Intent intent = new Intent();
				if (!mObjectPrefHelper.get(ItUser.class).isLoggedIn()){
					// New User
					intent.setClass(mActivity, LoginActivity.class);
				} else{
					// Has Loggined
					intent.setClass(mActivity, MainActivity.class);			
				}
				startActivity(intent);
			}
		}
	}
}
