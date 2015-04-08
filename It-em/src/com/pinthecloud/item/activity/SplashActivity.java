package com.pinthecloud.item.activity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.ItIntentService;
import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.GcmRegistrationIdEvent;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;

public class SplashActivity extends ItActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if(mApp.isAdmin()){
			Toast.makeText(mThisActivity, "Debugging : " + ItApplication.isDebugging(), Toast.LENGTH_LONG).show();;
		}

		// Remove noti
		NotificationManager notificationManger = (NotificationManager) mThisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManger.cancel(ItIntentService.NOTIFICATION_ID);

		// Check google play service
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mThisActivity) != ConnectionResult.SUCCESS) {
			installGooglePlayService();
			return;
		}

		// Check update
		float lastVersion = mPrefHelper.getFloat(ItConstant.APP_VERSION_KEY);
		float clientVersion = getClientAppVersion();
		if(lastVersion < clientVersion){
			mPrefHelper.put(ItConstant.APP_VERSION_KEY, clientVersion);
		} else if(lastVersion > clientVersion) {
			updateApp();
			return;
		}

		if(mPrefHelper.getInt(ItConstant.MAX_TEXTURE_SIZE_KEY) == PrefHelper.DEFAULT_INT){
			FrameLayout layout = (FrameLayout) findViewById(R.id.splash_surface_layout);
			layout.addView(new GetMaxTextureSizeSurfaceView(mThisActivity));
		} else {
			runItem();
		}
	}


	@Override
	public View getToolbarLayout() {
		return null;
	}


	private void runItem() {
		// If mobile id doesn't exist, get it
		ItDevice device = mObjectPrefHelper.get(ItDevice.class);
		if(device.getMobileId().equals(PrefHelper.DEFAULT_STRING)) {
			String mobileId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
			device.setMobileId(mobileId);
			mObjectPrefHelper.put(device);
		}

		// If registration id doesn't exist, get it
		device = mObjectPrefHelper.get(ItDevice.class);
		if(device.getRegistrationId().equals(PrefHelper.DEFAULT_STRING)) {
			setRegistrationId();
			return;
		}

		gotoNextActivity();
	}


	private void installGooglePlayService(){
		String message = getResources().getString(R.string.google_play_services_message);
		ItAlertDialog gcmDialog = ItAlertDialog.newInstance(message, null, null, null, false, true);
		gcmDialog.setCallback(new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ItConstant.GOOGLE_PLAY_SERVICE_APP_ID));
				startActivity(intent);
				finish();
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing
			}
			@Override
			public void doNegative(Bundle bundle) {
				finish();
			}
		});
		gcmDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
	}


	private void updateApp(){
		String message = getResources().getString(R.string.update_app_message);
		ItAlertDialog updateDialog = ItAlertDialog.newInstance(message, null, null, null, false, true);
		updateDialog.setCallback(new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ItConstant.GOOGLE_PLAY_APP_ID));
				startActivity(intent);
				finish();
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing				
			}
			@Override
			public void doNegative(Bundle bundle) {
				finish();
			}
		});
		updateDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
	}


	private float getClientAppVersion() {
		String versionName = "0.1";
		try {
			versionName = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return Float.parseFloat(versionName);
	}


	private void gotoNextActivity() {
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){

			@Override
			public void run() {
				ItUser user = mObjectPrefHelper.get(ItUser.class);
				Intent intent = new Intent();
				if (!user.checkLoggedIn()){
					// New User
					intent.setClass(mThisActivity, LoginActivity.class);
				} else {
					// Has Loggined
					intent.setClass(mThisActivity, MainActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, 1000);
	}


	private void setRegistrationId() {
		// Get registration id
		mDeviceHelper.getRegistrationIdAsync(mThisActivity, new EntityCallback<String>() {

			@Override
			public void onCompleted(String entity) {
				if(entity != null){
					onEvent(new GcmRegistrationIdEvent(entity));
				} else {
					// Get registration id in ItBroadCastReceiver.class
					// After get id, goto OnEvent()
				}
			}
		});
	}


	public void onEvent(GcmRegistrationIdEvent event){
		ItDevice deviceInfo = mObjectPrefHelper.get(ItDevice.class);
		deviceInfo.setRegistrationId(event.getRegistrationId());
		mObjectPrefHelper.put(deviceInfo);

		runItem();
	}


	private class GetMaxTextureSizeSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

		public GetMaxTextureSizeSurfaceView(Context context) {
			super(context);
			setRenderer(this);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			setMaxTextureSize();
			runItem();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
		}

		@Override
		public void onDrawFrame(GL10 gl) {
		}

		private void setMaxTextureSize(){
			int[] maxTextureSize = new int[1];
			GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
			mPrefHelper.put(ItConstant.MAX_TEXTURE_SIZE_KEY, maxTextureSize[0]);
		}
	}
}
