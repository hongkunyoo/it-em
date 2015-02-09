package com.pinthecloud.item.activity;

import java.io.IOException;
import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.facebook.AppEventsLogger;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class LoginActivity extends ItActivity {

	private com.facebook.widget.LoginButton mFacebookButton;
	private UiLifecycleHelper mFacebookUiHelper;

	private com.kakao.widget.LoginButton mKakaoButton;
	private SessionCallback mKakaoSessionCallback;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.activity_login);
		
		// Facebook
		mFacebookUiHelper = new UiLifecycleHelper(mThisActivity, new StatusCallback() {

			@Override
			public void call(com.facebook.Session session, SessionState state, Exception exception) {
			}
		});
		mFacebookUiHelper.onCreate(savedInstanceState);
		
		// Kakao
		mKakaoSessionCallback = new SessionCallback() {

			@Override
			public void onSessionOpened() {
			}
			@Override
			public void onSessionClosed(final KakaoException exception) {
				mKakaoButton.setVisibility(View.VISIBLE);
			}
		};
		
		// Set Activity
		findComponent();
		setButton();
	}


	@Override
	public void onResume() {
		super.onResume();

		// Facebook
		mFacebookUiHelper.onResume();
		AppEventsLogger.activateApp(mThisActivity);

		// Kakao
		if(com.kakao.Session.initializeSession(mThisActivity, mKakaoSessionCallback)){
			// In Progress
			mKakaoButton.setVisibility(View.GONE);
		} else if (com.kakao.Session.getCurrentSession().isOpened()){
			// Already Opened
			kakaoLogin();
		}
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
		AppEventsLogger.deactivateApp(mThisActivity);
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


	@Override
	public View getToolbarLayout() {
		return null;
	}


	private void findComponent(){
		mFacebookButton = (com.facebook.widget.LoginButton)findViewById(R.id.login_facebook);
		mKakaoButton = (com.kakao.widget.LoginButton)findViewById(R.id.login_kakao);
	}


	private void setButton(){
		mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email"));
		mFacebookButton.setBackgroundResource(R.drawable.signin_button);
		mFacebookButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		mFacebookButton.setTypeface(mFacebookButton.getTypeface(), Typeface.BOLD);
		mFacebookButton.setText(getResources().getString(R.string.facebook_login));
		mFacebookButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
		mFacebookButton.setTextColor(getResources().getColor(R.color.brand_color));
		mFacebookButton.setUserInfoChangedCallback(new com.facebook.widget.LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				com.facebook.Session session = com.facebook.Session.getActiveSession();
				if (session != null && session.isOpened() || user != null) {
					facebookLogin(session, user);
				}
			}
		});

		mKakaoButton.setBackgroundResource(R.drawable.signin_button);
		mKakaoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		mKakaoButton.setTypeface(mKakaoButton.getTypeface(), Typeface.BOLD);
		mKakaoButton.setText(getResources().getString(R.string.kakao_login));
		mKakaoButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
		mKakaoButton.setTextColor(getResources().getColor(R.color.brand_color));
	}


	private void facebookLogin(com.facebook.Session session, final GraphUser user){
		ItUser myUser = mObjectPrefHelper.get(ItUser.class);
		myUser.setRegistrationId(mPrefHelper.getString(ItConstant.REGISTRATION_ID_KEY));
		myUser.setMobileId(mPrefHelper.getString(ItConstant.MOBILE_ID_KEY));

		ItUser itUser = new ItUser(user.getId(), ItUser.PLATFORM.FACEBOOK, user.getFirstName().replace(" ", "_"),
				"", "", ItUser.TYPE.VIEWER, myUser.getRegistrationId(), myUser.getMobileId());
		itemLogin(itUser, "https://graph.facebook.com/"+itUser.getItUserId()+"/picture?type=large");
	}


	private void kakaoLogin() {
		UserManagement.requestMe(new MeResponseCallback() {

			@Override
			protected void onSuccess(final UserProfile userProfile) {
				ItUser myUser = mObjectPrefHelper.get(ItUser.class);
				myUser.setRegistrationId(mPrefHelper.getString(ItConstant.REGISTRATION_ID_KEY));
				myUser.setMobileId(mPrefHelper.getString(ItConstant.MOBILE_ID_KEY));

				ItUser itUser = new ItUser(""+userProfile.getId(), ItUser.PLATFORM.KAKAO, userProfile.getNickname(),
						"", "", ItUser.TYPE.VIEWER, myUser.getRegistrationId(), myUser.getMobileId());
				itemLogin(itUser, userProfile.getProfileImagePath());
			}

			@Override
			protected void onNotSignedUp() {
				EventBus.getDefault().post(new ItException("onNotSignedUp", ItException.TYPE.INTERNAL_ERROR));
			}

			@Override
			protected void onSessionClosedFailure(final APIErrorResult errorResult) {
				EventBus.getDefault().post(new ItException("onSessionClosedFailure", ItException.TYPE.INTERNAL_ERROR, errorResult));
			}

			@Override
			protected void onFailure(final APIErrorResult errorResult) {
				EventBus.getDefault().post(new ItException("onFailure", ItException.TYPE.INTERNAL_ERROR, errorResult));
			}
		});
	}


	private void itemLogin(final ItUser itUser, final String imageUrl) {
		mApp.showProgressDialog(mThisActivity);
		AsyncChainer.asyncChain(mThisActivity, new Chainable(){

			@Override
			public void doNext(Object object, Object... params) {
				addItUser(object, itUser);
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				getProfileImageFromService(obj, imageUrl);
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				uploadProfileImage(obj, itUser, (Bitmap)params[0]);
			}
		});
	}


	private void addItUser(final Object obj, final ItUser itUser){
		mUserHelper.add(itUser, new PairEntityCallback<ItUser, Exception>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception) {
				mObjectPrefHelper.put(entity);

				// If a new user, add it and get profile image.
				// Otherwise, go to next activity.
				if(exception == null) {
					itUser.setId(entity.getId());
					AsyncChainer.notifyNext(obj);
				} else {
					if(entity.getRegistrationId() == null){
						// For under ver 107
						entity.setRegistrationId(mPrefHelper.getString(ItConstant.REGISTRATION_ID_KEY));
						entity.setMobileId(mPrefHelper.getString(ItConstant.MOBILE_ID_KEY));
						mObjectPrefHelper.put(entity);

						mUserHelper.update(entity, new EntityCallback<ItUser>() {

							@Override
							public void onCompleted(ItUser entity) {
								goToNextActivity();
								AsyncChainer.clearChain(obj);
							}
						});
					} else {
						goToNextActivity();
						AsyncChainer.clearChain(obj);	
					}
				}
			}
		});
	}


	private void getProfileImageFromService(final Object obj, final String url){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap bitmap = null;
				try {
					bitmap = mApp.getPicasso()
							.load(url)
							.get();
				} catch (IOException e) {
					EventBus.getDefault().post(new ItException("getProfileImageFromFacebook", ItException.TYPE.INTERNAL_ERROR));
				}
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				AsyncChainer.notifyNext(obj, (Object)result);
			};
		}).execute();
	}


	private void uploadProfileImage(Object obj, final ItUser itUser, final Bitmap profileImage){
		AsyncChainer.asyncChain(mThisActivity, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(2);

				Bitmap profileImageBitmap = ImageUtil.refineSquareImage(profileImage, ImageUtil.PROFILE_IMAGE_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.USER_PROFILE, itUser.getId(), 
						profileImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				Bitmap profileThumbnailImageBitmap = ImageUtil.refineSquareImage(profileImage, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.USER_PROFILE, itUser.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX, 
						profileThumbnailImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				goToNextActivity();
			}
		});
	}


	private void goToNextActivity(){
		mApp.dismissProgressDialog();
		Intent intent = new Intent(mThisActivity, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
