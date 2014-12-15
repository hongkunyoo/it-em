package com.pinthecloud.item.fragment;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.BitmapUtil;
import com.squareup.picasso.Picasso;

public class LoginFragment extends ItFragment {

	private LoginButton mFacebookButton;
	private UiLifecycleHelper mUiHelper;

	private boolean mIsProfileImageUploaded = false;
	private boolean mIsSmallProfileImageUploaded = false;

	private Session.StatusCallback mCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback mDialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		}
		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUiHelper = new UiLifecycleHelper(mActivity, mCallback);
		mUiHelper.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		findComponent(view);
		setButton();
		return view;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mUiHelper.onSaveInstanceState(outState);
	}


	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}
		mUiHelper.onResume();
		AppEventsLogger.activateApp(mActivity);
	}


	@Override
	public void onPause() {
		super.onPause();
		mUiHelper.onPause();
		AppEventsLogger.deactivateApp(mActivity);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		mUiHelper.onDestroy();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mUiHelper.onActivityResult(requestCode, resultCode, data, mDialogCallback);
	}


	private void findComponent(View view){
		mFacebookButton = (LoginButton)view.findViewById(R.id.login_frag_facebook_button);
	}


	private void setButton(){
		mFacebookButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				Session session = Session.getActiveSession();
				if (session != null && session.isOpened() || user != null) {
					facebookLogin(user);
				}
			}
		});
	}


	private void facebookLogin(final GraphUser user){
		mApp.showProgressDialog(mActivity);

		final ItUser itUser = new ItUser();
		itUser.setItUserId(user.getId());
		itUser.setNickName(user.getFirstName());
		itUser.setSelfIntro("");
		itUser.setWebPage(""); // e.g. https://athere.blob.core.windows.net/userprofile/ID

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				mUserHelper.add(frag, itUser, new ItEntityCallback<ItUser>() {

					@Override
					public void onCompleted(ItUser entity) {
						mObjectPrefHelper.put(entity);
						itUser.setId(entity.getId());
						AsyncChainer.notifyNext(frag);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				new AsyncTask<Void,Void,Bitmap>(){

					@Override
					protected Bitmap doInBackground(Void... params) {
						Bitmap bitmap = null;
						try {
							bitmap = Picasso.with(mActivity).load("https://graph.facebook.com/"+user.getId()+"/picture?type=large").get();
						} catch (IOException e) {
							ExceptionManager.fireException(new ItException(frag, "facebookLogin", ItException.TYPE.SERVER_ERROR));
						}
						return bitmap;
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						AsyncChainer.notifyNext(frag, (Object)result);
					};
				}.execute();
			}
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				Bitmap profileImage = (Bitmap)params[0];
				profileImage = BitmapUtil.decodeInSampleSize(profileImage, BitmapUtil.BIG_SIZE, BitmapUtil.BIG_SIZE);
				Bitmap smallProfileImage = BitmapUtil.decodeInSampleSize(profileImage, BitmapUtil.SMALL_SIZE, BitmapUtil.SMALL_SIZE);

				blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, itUser.getId(), 
						profileImage, new ItEntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						mIsProfileImageUploaded = true;
						if(mIsSmallProfileImageUploaded){
							goToNextActivity();
						}
					}
				});

				blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, itUser.getId()+BitmapUtil.SMALL_POSTFIX, 
						smallProfileImage, new ItEntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						mIsSmallProfileImageUploaded = true;
						if(mIsProfileImageUploaded){
							goToNextActivity();	
						}
					}
				});
			}
		});
	}


	private void goToNextActivity(){
		mApp.dismissProgressDialog();
		Intent intent = new Intent(mActivity, MainActivity.class);
		startActivity(intent);
		mActivity.finish();
	}


	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	}
}
