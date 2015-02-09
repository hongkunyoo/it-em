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
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.ItIntentService;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.GCMRegIdEvent;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.AppVersion;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;

import de.greenrobot.event.EventBus;

public class SplashFragment extends ItFragment {

	private View mProgressLayout;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(mThisFragment);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_splash, container, false);
		findComponent(view);
		if(mPrefHelper.getInt(ItConstant.MAX_TEXTURE_SIZE_KEY) == PrefHelper.DEFAULT_INT){
			FrameLayout layout = (FrameLayout) view.findViewById(R.id.splash_frag_surface_layout);
			layout.addView(new GetMaxTextureSizeSurfaceView(mActivity));
		} else {
			runItem();
		}
		return view;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(mThisFragment);
	}


	private void findComponent(View view){
		mProgressLayout = view.findViewById(R.id.splash_frag_progress_layout);
	}


	private void runItem() {
		if (mApp.isAdmin()){
			Toast.makeText(mActivity, "Debugging : " + ItApplication.isDebugging(), Toast.LENGTH_SHORT).show();
		}

		NotificationManager notificationManger = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManger.cancel(ItIntentService.NOTIFICATION_ID);

		mVersionHelper.getServerAppVersionAsync(new EntityCallback<AppVersion>() {

			@Override
			public void onCompleted(AppVersion serverVer) {
				double clientVer = mVersionHelper.getClientAppVersion();
				if (serverVer.getVersion() > clientVer) {
					updateApp(serverVer);
				} else {
					goToNextActivity();
				}
			}
		});
	}


	private void updateApp(final AppVersion serverVer){
		String message = getResources().getString(R.string.update_app_message);
		ItAlertDialog updateDialog = ItAlertDialog.newInstance(message, null, null, true);

		updateDialog.setCallback(new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ItConstant.GOOGLE_PLAY_APP_ID));
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
			// If registration id doesn't exist, get it
			if(mPrefHelper.getString(ItConstant.REGISTRATION_ID_KEY).equals(PrefHelper.DEFAULT_STRING)) {
				getRegistrationId(mThisFragment);
				return;
			}

			ItUser user = mObjectPrefHelper.get(ItUser.class);
			Intent intent = new Intent();
			if (!user.isLoggedIn()){
				// New User
				intent.setClass(mActivity, LoginActivity.class);
			} else {
				// Has Loggined
				intent.setClass(mActivity, MainActivity.class);
			}
			startActivity(intent);
		}
	}


	private void getRegistrationId(final ItFragment frag) {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity) == ConnectionResult.SUCCESS) {
			// Get registration id
			mProgressLayout.setVisibility(View.VISIBLE);
			mUserHelper.getRegistrationIdAsync(mActivity, new EntityCallback<String>() {

				@Override
				public void onCompleted(String entity) {
					if(entity != null){
						ItUser user = mObjectPrefHelper.get(ItUser.class);
						user.setRegistrationId(entity);
						mObjectPrefHelper.put(user);

						onEvent(new GCMRegIdEvent(entity));
					} else {
						// Get registration id in ItBroadCastReceiver.class
						// After get id, goto OnEvent()
					}
				}
			});
		} else {
			// Show dialog user to Install google play service
			String message = getResources().getString(R.string.google_play_services_message);
			ItAlertDialog gcmDialog = ItAlertDialog.newInstance(message, null, null, true);
			gcmDialog.setCallback(new DialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://details?id=" + ItConstant.GOOGLE_PLAY_SERVICE_APP_ID));
					startActivity(intent);
					mActivity.finish();
				}

				@Override
				public void doNegativeThing(Bundle bundle) {
					mActivity.finish();
				}
			});
			gcmDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
		}
	}


	public void onEvent(GCMRegIdEvent event){
		if(isAdded()){
			// Get mobile id
			final ItUser user = mObjectPrefHelper.get(ItUser.class);
			String mobileId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
			user.setMobileId(mobileId);

			AsyncChainer.asyncChain(mThisFragment, new Chainable(){

				@Override
				public void doNext(Object obj, Object... params) {
					if(user.isLoggedIn()){
						// For under ver 107
						updateUser(obj, user);
					} else {
						AsyncChainer.notifyNext(obj);
					}
				}
			}, new Chainable(){

				@Override
				public void doNext(Object obj, Object... params) {
					mProgressLayout.setVisibility(View.GONE);

					mObjectPrefHelper.put(user);
					mPrefHelper.put(ItConstant.REGISTRATION_ID_KEY, user.getRegistrationId());
					mPrefHelper.put(ItConstant.MOBILE_ID_KEY, user.getMobileId());
					goToNextActivity();
				}
			});
		}
	}


	private void updateUser(final Object obj, final ItUser itUser) {
		mUserHelper.update(itUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				AsyncChainer.notifyNext(obj);
			}
		});
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
