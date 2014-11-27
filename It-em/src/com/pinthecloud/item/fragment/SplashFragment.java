package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.HongkunTestActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.helper.PrefHelper;

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
		try {
			Class.forName("com.pinthecloud.item.util.HongUtil");
			Intent hongTent = new Intent();
			hongTent.setClass(mActivity, HongkunTestActivity.class);
			startActivity(hongTent);
		} catch (ClassNotFoundException e) {
			if(mThisFragment.isAdded()){
				Intent intent = new Intent();
				if (!mPrefHelper.getBoolean(PrefHelper.IS_LOGIN_KEY)){
					// New User
					intent.setClass(mActivity, LoginActivity.class);
				} else{
					// Has Logined
					intent.setClass(mActivity, MainActivity.class);
				}
				intent.setClass(mActivity, ItemActivity.class);
				startActivity(intent);
			}
		}
	}
}
