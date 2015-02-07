package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.exception.KakaoException;
//import com.facebook.Session;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class SettingsFragment extends ItFragment {

	private TextView mNickName;
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
		setAdminComponent(view);
		return view;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.settings));
	}


	private void findComponent(View view){
		mNickName = (TextView)view.findViewById(R.id.settings_frag_nick_name);
		mLogout = (RelativeLayout)view.findViewById(R.id.settings_frag_logout);
	}


	private void setComponent(){
		mNickName.setText(mMyItUser.getNickName());
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
						EntityCallback<Boolean> logoutCallback = new EntityCallback<Boolean>() {

							@Override
							public void onCompleted(Boolean entity) {
								if (entity) {
									removePreference();
									mApp.dismissProgressDialog();
									Intent intent = new Intent(mActivity, LoginActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
								}
							}
						};

						mApp.showProgressDialog(mActivity);
						if (mMyItUser.getPlatform().equalsIgnoreCase(ItUser.PLATFORM.FACEBOOK.toString())) {
							facebookLogout(logoutCallback);
						} else if (mMyItUser.getPlatform().equalsIgnoreCase(ItUser.PLATFORM.KAKAO.toString())) {
							kakaoLogout(logoutCallback);
						}
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


	private void setAdminComponent(View view) {
		if (!mApp.isAdmin()) return;

		RadioGroup rg = new RadioGroup(mActivity);
		RadioButton real = new RadioButton(mActivity);
		RadioButton test = new RadioButton(mActivity);

		real.setId(100001);
		real.setText("Real");
		real.setChecked(!ItApplication.isDebugging());

		test.setId(100002);
		test.setText("Test");
		test.setChecked(ItApplication.isDebugging());

		rg.addView(real);
		rg.addView(test);

		LinearLayout layout = (LinearLayout)view.findViewById(R.id.settings_frag_layout);
		layout.addView(rg);

		real.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);
				mPrefHelper.put(ItConstant.CURRENT_MODE, ItApplication.REAL);
				mApp.switchClient(new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						switchClientCallback();
					}
				});
			}
		});

		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);
				mPrefHelper.put(ItConstant.CURRENT_MODE, ItApplication.TEST);
				mApp.switchClient(new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						switchClientCallback();
					}
				});
			}
		});
	}


	private void switchClientCallback(){
		mApp.dismissProgressDialog();
		Intent intent = new Intent(mActivity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	private void facebookLogout(EntityCallback<Boolean> callback){
		com.facebook.Session session = com.facebook.Session.getActiveSession();
		if (session == null) {
			session = new com.facebook.Session(mActivity);
			com.facebook.Session.setActiveSession(session);
		}
		session.closeAndClearTokenInformation();
		callback.onCompleted(true);
	}


	private void kakaoLogout(final EntityCallback<Boolean> callback){

		boolean initalizing = com.kakao.Session.initializeSession(mActivity, new SessionCallback() {

			@Override
			public void onSessionOpened() {
			}
			@Override
			public void onSessionClosed(final KakaoException exception) {
			}
		});

		if (!initalizing && com.kakao.Session.getCurrentSession().isOpened()){
			UserManagement.requestLogout(new LogoutResponseCallback() {

				@Override
				protected void onSuccess(long userId) {
					callback.onCompleted(true);
				}

				@Override
				protected void onFailure(APIErrorResult errorResult) {
					callback.onCompleted(false);
					EventBus.getDefault().post(new ItException("onFailure", ItException.TYPE.INTERNAL_ERROR, errorResult));
				}
			});
		}
	}


	private void removePreference(){
		mObjectPrefHelper.remove(ItUser.class);
	}
}
