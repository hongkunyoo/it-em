package com.pinthecloud.item.fragment;

import java.io.IOException;
import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.ItLog;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class LoginFragment extends ItFragment {

	private LoginButton mFacebookButton;
	private UiLifecycleHelper mFacebookUiHelper;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFacebookUiHelper = new UiLifecycleHelper(mActivity, new StatusCallback() {

			@Override
			public void call(Session session, SessionState state, Exception exception) {
			}
		});
		mFacebookUiHelper.onCreate(savedInstanceState);
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
	public void onResume() {
		super.onResume();
		mFacebookUiHelper.onResume();
		AppEventsLogger.activateApp(mActivity);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onPause() {
		super.onPause();
		mFacebookUiHelper.onPause();
		AppEventsLogger.deactivateApp(mActivity);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		mFacebookUiHelper.onDestroy();
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mFacebookUiHelper.onSaveInstanceState(outState);
	}


	private void findComponent(View view){
		mFacebookButton = (LoginButton)view.findViewById(R.id.login_frag_facebook_button);
	}


	private void setButton(){
		mFacebookButton.setFragment(mThisFragment);
		mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email"));
		mFacebookButton.setBackgroundResource(R.drawable.signin_facebook_button);
		mFacebookButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		mFacebookButton.setTypeface(mFacebookButton.getTypeface(), Typeface.BOLD);
		mFacebookButton.setText(getResources().getString(R.string.facebook_login));
		mFacebookButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
		mFacebookButton.setTextColor(getResources().getColor(R.color.brand_color));
		mFacebookButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				Session session = Session.getActiveSession();
				if (session != null && session.isOpened() || user != null) {
					facebookLogin(session, user);
				}
			}
		});
	}


	private void facebookLogin(Session session, final GraphUser user){
		mApp.showProgressDialog(mActivity);

		String email = user.getProperty("email") == null ? user.getId() : user.getProperty("email").toString();
		final ItUser itUser = new ItUser(user.getId(), ItUser.FACEBOOK, email,
				user.getFirstName().replace(" ", "_"), "", "", ItUser.TYPE.VIEWER);
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				// Get registration Id from Google Cloud Service
				getRegistrationId(frag, itUser);
			}
		}, new Chainable(){
			
			@Override
			public void doNext(ItFragment frag, Object... params) {
				addItUser(frag, itUser);
			}
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				getProfileImageFromFacebook(frag, user);
			}
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				uploadProfileImage(frag, itUser, (Bitmap)params[0]);
			}
		});
	}


	private void getRegistrationId(final ItFragment frag, final ItUser itUser) {
		String androidId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
		itUser.setMobileId(androidId);
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity) == ConnectionResult.SUCCESS) {
			mUserHelper.getRegistrationIdAsync(frag, new EntityCallback<String>(){

				@Override
				public void onCompleted(String registrationId) {
					itUser.setRegistrationId(registrationId);
					ItLog.log(itUser);
					AsyncChainer.notifyNext(frag);
				}
			});
		}else{
			EventBus.getDefault().post(new ItException("getRegistrationId", ItException.TYPE.GCM_REGISTRATION_FAIL));
		}
	}


	private void addItUser(final ItFragment frag, final ItUser itUser){
		mUserHelper.add(itUser, new PairEntityCallback<ItUser, Exception>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception) {
				mObjectPrefHelper.put(entity);

				// If a new user, add it and get profile image.
				// Otherwise, go to next activity.
				if(exception == null) {
					itUser.setId(entity.getId());
					AsyncChainer.notifyNext(frag);
				} else {
					ItUser user = mObjectPrefHelper.get(ItUser.class);
					user.setRegistrationId(null);
					mObjectPrefHelper.put(user);
					
					goToNextActivity();
					AsyncChainer.clearChain(frag);
				}
			}
		});
	}


	private void getProfileImageFromFacebook(final ItFragment frag, final GraphUser user){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap bitmap = null;
				try {
					bitmap = mApp.getPicasso()
							.load("https://graph.facebook.com/"+user.getId()+"/picture?type=large")
							.resize(ImageUtil.PROFILE_IMAGE_SIZE, ImageUtil.PROFILE_IMAGE_SIZE)
							.get();
				} catch (IOException e) {
					EventBus.getDefault().post(new ItException("getProfileImageFromFacebook", ItException.TYPE.SERVER_ERROR));
				}
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				AsyncChainer.notifyNext(frag, (Object)result);
			};
		}).execute();
	}


	private void uploadProfileImage(ItFragment frag, final ItUser itUser, final Bitmap profileImage){
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(2);

				Bitmap profileImageBitmap = ImageUtil.refineSquareImage(profileImage, ImageUtil.PROFILE_IMAGE_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.USER_PROFILE, itUser.getId(), 
						profileImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});

				Bitmap profileThumbnailImageBitmap = ImageUtil.refineSquareImage(profileImage, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.USER_PROFILE, itUser.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX, 
						profileThumbnailImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				goToNextActivity();
			}
		});
	}


	private void goToNextActivity(){
		mApp.dismissProgressDialog();
		Intent intent = new Intent(mActivity, MainActivity.class);
		startActivity(intent);
		mActivity.finish();
	}
}
