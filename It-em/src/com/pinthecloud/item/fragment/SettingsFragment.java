package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

public class SettingsFragment extends ItFragment {

	private TextView mEmail;
	private RelativeLayout mLogout;
	
	private RadioGroup rg;

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
		setAdminComponent(view);
		setButton();
		
		return view;
	}
	
	private void setAdminComponent(View view) {
		if (!this.mApp.isAdmin()) return;
		
		rg = new RadioGroup(this.mActivity);
		RadioButton real = new RadioButton(this.mActivity);
		RadioButton test = new RadioButton(this.mActivity);
		real.setText("Real");
		real.setId(100001);
		test.setText("Test");
		test.setId(100002);
		test.setChecked(true);
		
		rg.addView(real);
		rg.addView(test);
		mLogout.addView(rg);
		
		real.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mApp.showProgressDialog(mActivity);
				mApp.switchClient(ItApplication.REAL, new EntityCallback<Boolean>() {
					
					@Override
					public void onCompleted(Boolean entity) {
						// TODO Auto-generated method stub
						mApp.dismissProgressDialog();
					}
				});
			}
		});
		
		test.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mApp.showProgressDialog(mActivity);
				mApp.switchClient(ItApplication.TEST, new EntityCallback<Boolean>() {
					
					@Override
					public void onCompleted(Boolean entity) {
						// TODO Auto-generated method stub
						mApp.dismissProgressDialog();
					}
				});
			}
		});
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.settings));
	}


	private void findComponent(View view){
		mEmail = (TextView)view.findViewById(R.id.settings_frag_email);
		mLogout = (RelativeLayout)view.findViewById(R.id.settings_frag_logout);
	}


	private void setComponent(){
		mEmail.setText(mMyItUser.getEmail());
	}


	private void setButton(){
		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = getResources().getString(R.string.logout_message);
				ItAlertDialog logoutDialog = ItAlertDialog.newInstance(message, null, null, true);
				logoutDialog.setCallback(new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						if(mMyItUser.getPlatform().equals(ItUser.FACEBOOK)){
							facebookLogout();
						}

						removePreference();

						Intent intent = new Intent(mActivity, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				});
				logoutDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void facebookLogout(){
		Session session = Session.getActiveSession();
		if (session != null) {
			if (session.isOpened()) {
				session.closeAndClearTokenInformation();
				return;
			}
		}
		session = new Session(mActivity);
		Session.setActiveSession(session);
		session.closeAndClearTokenInformation();
	}


	private void removePreference(){
		mObjectPrefHelper.remove(ItUser.class);
	}
}
