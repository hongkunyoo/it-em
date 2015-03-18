package com.pinthecloud.item.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class SettingsActivity extends ItActivity {

	private final int PROFILE_SETTINGS = 0;
	
	private View mToolbarLayout;
	private Toolbar mToolbar;
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_settings);

		mMyItUser = mObjectPrefHelper.get(ItUser.class);
		setToolbar();
		findComponent();
		setComponent();
		setButton();
		setProfile();
		setAdminComponent();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


	@Override
	public void onStart() {
		super.onStart();
		mUserHabitHelper.activityStart(mThisActivity);
		mGaHelper.reportActivityStart(mThisActivity);
	}


	@Override
	public void onStop() {
		super.onStop();
		mUserHabitHelper.activityStop(mThisActivity);
		mGaHelper.reportActivityStop(mThisActivity);
	}
	
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra(ItUser.INTENT_KEY, mObjectPrefHelper.get(ItUser.class));
		setResult(Activity.RESULT_OK, intent);
		
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_right);
	}


	@Override
	public View getToolbarLayout() {
		return mToolbarLayout;
	}
	
	
	private void setToolbar(){
		mToolbarLayout = findViewById(R.id.toolbar_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		        onBackPressed();
		    }
		});
	}


	private void findComponent(){
		mProfileSettings = findViewById(R.id.settings_profile_settings);
		mProSettings = findViewById(R.id.settings_pro_settings);
		mProSettingsText = (TextView)findViewById(R.id.settings_pro_settings_text);
		mNotiMyItem = (ToggleButton)findViewById(R.id.settings_noti_my_item);
		mNotiItItem = (ToggleButton)findViewById(R.id.settings_noti_it_item);
		mNotiReplyItem = (ToggleButton)findViewById(R.id.settings_noti_reply_item);
		mNickName = (TextView)findViewById(R.id.settings_nick_name);
		mLogout = (RelativeLayout)findViewById(R.id.settings_logout);
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
				Intent intent = new Intent(mThisActivity, ProfileSettingsActivity.class);
				startActivityForResult(intent, PROFILE_SETTINGS);
			}
		});

		mProSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mThisActivity, 
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
						mApp.showProgressDialog(mThisActivity);
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
				logoutDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setProfile(){
		mNickName.setText(mMyItUser.getNickName());
	}


	private void setAdminComponent() {
		if (!mApp.isAdmin()) return;

		RadioGroup rg = new RadioGroup(mThisActivity);
		RadioButton real = new RadioButton(mThisActivity);
		RadioButton test = new RadioButton(mThisActivity);

		real.setId(100001);
		real.setText("Real");
		real.setChecked(!ItApplication.isDebugging());

		test.setId(100002);
		test.setText("Test");
		test.setChecked(ItApplication.isDebugging());

		rg.addView(real);
		rg.addView(test);

		LinearLayout layout = (LinearLayout)findViewById(R.id.settings_layout);
		layout.addView(rg);

		real.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mThisActivity);
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
				mApp.showProgressDialog(mThisActivity);
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
		Intent intent = new Intent(mThisActivity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	private void facebookLogout(){
		com.facebook.Session session = com.facebook.Session.getActiveSession();
		if (session == null) {
			session = new com.facebook.Session(mThisActivity);
			com.facebook.Session.setActiveSession(session);
		}
		session.closeAndClearTokenInformation();
		logout();
	}


	private void kakaoLogout(){
		boolean initalizing = com.kakao.Session.initializeSession(mThisActivity, new SessionCallback() {

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

				Intent intent = new Intent(mThisActivity, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}


	private void removePreference(){
		mObjectPrefHelper.remove(ItUser.class);
	}
}
