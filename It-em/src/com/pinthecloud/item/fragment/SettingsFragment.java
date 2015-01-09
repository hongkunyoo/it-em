package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.model.ItUser;

public class SettingsFragment extends ItFragment {

	private TextView mItUserId;
	private RelativeLayout mLogout;

	private ItUser mMyItUser;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();
		return view;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.settings));
	}


	private void findComponent(View view){
		mItUserId = (TextView)view.findViewById(R.id.settings_frag_it_user_id);
		mLogout = (RelativeLayout)view.findViewById(R.id.settings_frag_logout);
	}


	private void setComponent(){
		mItUserId.setText(mMyItUser.getItUserId());
	}


	private void setButton(){
		mLogout.setOnClickListener(new OnClickListener() {

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
