package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Session;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.model.ItUser;

public class SettingsFragment extends ItFragment {

	private Button mLogoutButton;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		setActionBar();
		findComponent(view);
		setButton();
		return view;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.settings));
	}


	private void findComponent(View view){
		mLogoutButton = (Button)view.findViewById(R.id.settings_frag_logout_button);
	}


	private void setButton(){
		mLogoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItUser myItUser = mObjectPrefHelper.get(ItUser.class);
				if(myItUser.getPlatform().equals(LoginFragment.FACEBOOK)){
					facebookLogout();
				}

				mObjectPrefHelper.remove(ItUser.class);

				Intent intent = new Intent(mActivity, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}


	private void facebookLogout(){
		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
			}
		} else {
			session = new Session(mActivity);
			Session.setActiveSession(session);
			session.closeAndClearTokenInformation();
		}
	}
}
