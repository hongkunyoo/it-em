package com.pinthecloud.item.fragment;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.AppVersion;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ViewUtil;

import de.greenrobot.event.EventBus;

public class SplashFragment extends ItFragment {

//	private final int SPLASH_TIME = 1000;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_splash, container, false);
		if(mPrefHelper.getInt(ViewUtil.MAX_TEXTURE_SIZE_KEY) == PrefHelper.DEFAULT_INT){
			FrameLayout layout = (FrameLayout) view.findViewById(R.id.splash_frag_surface_layout);
			layout.addView(new GetMaxTextureSizeSurfaceView(mActivity));
		} else {
			runItem();
		}
		return view;
	}


	private void runItem() {
		
		NotificationManager notiMan =
				(NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		notiMan.cancel(GlobalVariable.NOTIFICATION_ID);
		
		
		if (mApp.isAdmin())
			Toast.makeText(mActivity, "Debugging : " + ItApplication.isDebugging(), Toast.LENGTH_SHORT).show();
		
		mVersionHelper.getServerAppVersionAsync(new EntityCallback<AppVersion>() {

			@Override
			public void onCompleted(AppVersion serverVer) {
				double clientVer = mVersionHelper.getClientAppVersion();

				if (serverVer.getVersion() > clientVer) {
					updateApp(serverVer);
				}else{
					goToNextActivity();
				}
			}
		});
//		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				goToNextActivity();
//			}
//		}, SPLASH_TIME);
	}
	
	private void updateApp(final AppVersion serverVer){
//		mApp.removeMySquarePreference(thisFragment);
//		mApp.removeMyUserPreference(thisFragment);

		String message = getResources().getString(R.string.update_app_message);
		ItAlertDialog updateDialog = ItAlertDialog.newInstance(message, null, null, true);
		
		updateDialog.setCallback(new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + GlobalVariable.GOOGLE_PLAY_APP_ID));
				startActivity(intent);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				if (serverVer.getType().equals(AppVersion.TYPE.MANDATORY.toString())){
					mActivity.finish();
				} else {
					goToNextActivity();
				}
			}
		});
		updateDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
	
	}

	private void goToNextActivity() {
		if(isAdded()){
			Intent intent = new Intent();
			ItUser user = mObjectPrefHelper.get(ItUser.class); 
			if (!user.isLoggedIn()){
				// New User
				intent.setClass(mActivity, LoginActivity.class);
			} else { // Has Loggined
				// Check whether User has Registration Id
				if (user.getRegistrationId() == PrefHelper.DEFAULT_STRING) {
					handle_task_under_0_11();
					return;
				}
				intent.setClass(mActivity, MainActivity.class);
			}
			startActivity(intent);
		}
	}
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	/*
	 * HANDLING under version 0.11
	 */
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
	private void handle_task_under_0_11() {
		final ItUser itUser = mObjectPrefHelper.get(ItUser.class);
		
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){
			@Override
			public void doNext(ItFragment frag, Object... params) {
				// TODO Auto-generated method stub
				getRegistrationId_FOR_UNDER_0_11(frag, itUser);
			}
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				// TODO Auto-generated method stub
				updateUser_FOR_UNDER_0_11(frag, itUser);
			}
			
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mActivity, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void getRegistrationId_FOR_UNDER_0_11(final ItFragment frag, final ItUser itUser) {
		String androidId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
		itUser.setMobileId(androidId);
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity) == ConnectionResult.SUCCESS) {
			mUserHelper.getRegistrationIdAsync(frag, new EntityCallback<String>(){

				@Override
				public void onCompleted(String registrationId) {
					itUser.setRegistrationId(registrationId);
					AsyncChainer.notifyNext(frag);
				}
			});
		}else{
			EventBus.getDefault().post(new ItException("getRegistrationId", ItException.TYPE.GCM_REGISTRATION_FAIL));
		}
	}
	
	private void updateUser_FOR_UNDER_0_11(final ItFragment frag, final ItUser itUser) {
		mUserHelper.update(itUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mObjectPrefHelper.put(entity);
				AsyncChainer.notifyNext(frag);
			}
		});
	}
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////

	
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
			mPrefHelper.put(ViewUtil.MAX_TEXTURE_SIZE_KEY, maxTextureSize[0]);
		}
	}
}
