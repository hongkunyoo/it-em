package com.pinthecloud.item.fragment;

import android.app.Activity;
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
import android.widget.ToggleButton;

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.exception.KakaoException;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.BeProActivity;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.activity.ProSettingsActivity;
import com.pinthecloud.item.activity.ProfileSettingsActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class SettingsFragment extends ItFragment {

	private final int PROFILE_SETTINGS = 0;

	private LinearLayout mLayout;
	private View mProfileSettings;
	private View mProSettings;
	private TextView mProSettingsText;

	private ToggleButton mNotiMyItem;
	private ToggleButton mNotiItItem;
	private ToggleButton mNotiReplyItem;

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

		mGaHelper.sendScreen(mThisFragment);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();
		setProfile();
		setAdminComponent(view);

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROFILE_SETTINGS:
			if (resultCode == Activity.RESULT_OK){
				mMyItUser = data.getParcelableExtra(ItUser.INTENT_KEY);
				setProfile();
			}
			break;
		}
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.settings));
	}


	private void findComponent(View view){
		mLayout = (LinearLayout)view.findViewById(R.id.settings_layout);
		mProfileSettings = view.findViewById(R.id.settings_profile_settings);
		mProSettings = view.findViewById(R.id.settings_pro_settings);
		mProSettingsText = (TextView)view.findViewById(R.id.settings_pro_settings_text);
		mNotiMyItem = (ToggleButton)view.findViewById(R.id.settings_noti_my_item);
		mNotiItItem = (ToggleButton)view.findViewById(R.id.settings_noti_it_item);
		mNotiReplyItem = (ToggleButton)view.findViewById(R.id.settings_noti_reply_item);
		mNickName = (TextView)view.findViewById(R.id.settings_nick_name);
		mLogout = (RelativeLayout)view.findViewById(R.id.settings_logout);
	}


	private void setComponent(){
		String proSettings = getResources().getString(R.string.pro_settings);
		String bePro = getResources().getString(R.string.be_pro);
		mProSettingsText.setText(mMyItUser.checkPro() ? proSettings : bePro);
	}


	private void setButton(){
		mProfileSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ProfileSettingsActivity.class);
				startActivityForResult(intent, PROFILE_SETTINGS);
			}
		});

		mProSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, 
						mMyItUser.checkPro() ? ProSettingsActivity.class : BeProActivity.class);
				startActivity(intent);
			}
		});

		mNotiMyItem.setChecked(mMyItUser.isNotiMyItem());
		mNotiMyItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMyItUser.setNotiMyItem(mNotiMyItem.isChecked());
				mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

					@Override
					public void onCompleted(ItUser entity) {
						mMyItUser = entity;
						mObjectPrefHelper.put(mMyItUser);
						mNotiMyItem.setChecked(mMyItUser.isNotiMyItem());
					}
				});
			}
		});

		mNotiItItem.setChecked(mMyItUser.isNotiItItem());
		mNotiItItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMyItUser.setNotiItItem(mNotiItItem.isChecked());
				mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

					@Override
					public void onCompleted(ItUser entity) {
						mMyItUser = entity;
						mObjectPrefHelper.put(mMyItUser);
						mNotiItItem.setChecked(mMyItUser.isNotiMyItem());
					}
				});
			}
		});

		mNotiReplyItem.setChecked(mMyItUser.isNotiReplyItem());
		mNotiReplyItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMyItUser.setNotiReplyItem(mNotiReplyItem.isChecked());
				mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

					@Override
					public void onCompleted(ItUser entity) {
						mMyItUser = entity;
						mObjectPrefHelper.put(mMyItUser);
						mNotiReplyItem.setChecked(mMyItUser.isNotiMyItem());
					}
				});
			}
		});

		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = getResources().getString(R.string.logout_message);
				ItAlertDialog logoutDialog = ItAlertDialog.newInstance(message, null, null, true);

				logoutDialog.setCallback(new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						mApp.showProgressDialog(mActivity);
						if (mMyItUser.getPlatform().equalsIgnoreCase(ItUser.PLATFORM.FACEBOOK.toString())) {
							facebookLogout();
						} else if (mMyItUser.getPlatform().equalsIgnoreCase(ItUser.PLATFORM.KAKAO.toString())) {
							kakaoLogout();
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


	private void setProfile(){
		mNickName.setText(mMyItUser.getNickName());
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

		mLayout.addView(rg);

		real.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);
				mApp.switchClient(ItApplication.REAL, new EntityCallback<Boolean>() {

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
				mApp.switchClient(ItApplication.TEST, new EntityCallback<Boolean>() {

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


	private void facebookLogout(){
		com.facebook.Session session = com.facebook.Session.getActiveSession();
		if (session == null) {
			session = new com.facebook.Session(mActivity);
			com.facebook.Session.setActiveSession(session);
		}
		session.closeAndClearTokenInformation();
		logout();
	}


	private void kakaoLogout(){
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
					logout();
				}
				@Override
				protected void onFailure(APIErrorResult errorResult) {
					EventBus.getDefault().post(new ItException("onFailure", ItException.TYPE.INTERNAL_ERROR, errorResult));
				}
			});
		}
	}


	private void logout(){
		ItDevice device = mObjectPrefHelper.get(ItDevice.class);
		mDeviceHelper.del(device, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				mApp.dismissProgressDialog();
				removePreference();

				Intent intent = new Intent(mActivity, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}


	private void removePreference(){
		mObjectPrefHelper.remove(ItUser.class);
	}
}
