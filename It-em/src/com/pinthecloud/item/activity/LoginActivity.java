package com.pinthecloud.item.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.pinthecloud.item.R;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class LoginActivity extends ItActivity {

	private com.facebook.widget.LoginButton mFacebookButton;
	private UiLifecycleHelper mFacebookUiHelper;

	private com.kakao.widget.LoginButton mKakaoButton;
	private com.kakao.Session mKakaoSession;
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
			public void onSessionOpening() {
			}
			@Override
			public void onSessionOpened() {
				kakaoLogin();
			}
			@Override
			public void onSessionClosed(KakaoException exception) {
			}
		};

		mKakaoSession = com.kakao.Session.getCurrentSession();
		mKakaoSession.addCallback(mKakaoSessionCallback);

		// Set Activity
		findComponent();
		setButton();
	}


	@Override
	protected void onStart() {
		super.onStart();
		mGaHelper.reportActivityStart(mThisActivity);
	}


	@Override
	public void onResume() {
		super.onResume();

		// Facebook
		mFacebookUiHelper.onResume();
		AppEventsLogger.activateApp(mThisActivity);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (com.kakao.Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
			return;
		}

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
	protected void onStop() {
		super.onStop();
		mGaHelper.reportActivityStop(mThisActivity);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		mFacebookUiHelper.onDestroy();
		mKakaoSession.removeCallback(mKakaoSessionCallback);
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
		mFacebookButton.setReadPermissions(Arrays.asList("public_profile"));
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
					facebookLogin(user);
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


	private void facebookLogin(final GraphUser facebookUser){
		ItUser user = new ItUser(facebookUser.getId(), ItUser.PLATFORM.FACEBOOK, facebookUser.getFirstName().replace(" ", "_"), ItUser.TYPE.VIEWER);
		itemLogin(user, "https://graph.facebook.com/"+user.getItUserId()+"/picture?type=large");
	}


	private void kakaoLogin() {
		UserManagement.requestMe(new MeResponseCallback() {

			@Override
			protected void onSuccess(final UserProfile kakaoUser) {
				ItUser user = new ItUser(""+kakaoUser.getId(), ItUser.PLATFORM.KAKAO, kakaoUser.getNickname().replace(" ", "_"), ItUser.TYPE.VIEWER);
				itemLogin(user, kakaoUser.getProfileImagePath());
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


	private void itemLogin(final ItUser user, final String imageUrl) {
		mApp.showProgressDialog(mThisActivity);
		AsyncChainer.asyncChain(mThisActivity, new Chainable(){

			@Override
			public void doNext(Object object, Object... params) {
				ItDevice myDevice = mObjectPrefHelper.get(ItDevice.class);
				ItDevice device = new ItDevice(myDevice.getMobileId(), myDevice.getRegistrationId());
				signin(object, user, device);
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				final ItUser user = mObjectPrefHelper.get(ItUser.class);
				mBlobStorageHelper.isExistAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, user.getId(), new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						if(entity){
							goToNextActivity();
						} else {
							getProfileImageFromService(user, imageUrl);
						}
					}
				});
			}
		});
	}


	private void signin(final Object obj, final ItUser user, ItDevice device){
		mUserHelper.signin(user, device, new PairEntityCallback<ItUser, ItDevice>() {

			@Override
			public void onCompleted(ItUser user, ItDevice device) {
				mObjectPrefHelper.put(user);
				mObjectPrefHelper.put(device);
				AsyncChainer.notifyNext(obj);
			}
		});
	}


	private void getProfileImageFromService(final ItUser user, final String url){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				try {
					return mApp.getPicasso().load(url).get();
				} catch (IOException e) {
					return BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
				}
			}

			@Override
			protected void onPostExecute(Bitmap profileImage) {
				getProfileImage(user, profileImage);
			};
		}).execute();
	}


	private void getProfileImage(final ItUser user, final Bitmap originProfileImage){
		(new AsyncTask<Void,Void,List<Bitmap>>(){

			@Override
			protected List<Bitmap> doInBackground(Void... params) {
				Bitmap profileImage = ImageUtil.refineSquareImage(originProfileImage, ImageUtil.PROFILE_IMAGE_SIZE, false);
				Bitmap profileThumbnailImage = ImageUtil.refineSquareImage(originProfileImage, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE, false);

				List<Bitmap> profileImageList = new ArrayList<Bitmap>();
				profileImageList.add(profileImage);
				profileImageList.add(profileThumbnailImage);
				return profileImageList;
			}

			@Override
			protected void onPostExecute(List<Bitmap> profileImageList) {
				uploadProfileImage(user, profileImageList);
			};
		}).execute();
	}


	private void uploadProfileImage(final ItUser user, final List<Bitmap> profileImageList){
		AsyncChainer.asyncChain(mThisActivity, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(2);

				Bitmap profileImage = profileImageList.get(0);
				Bitmap profileThumbnailImage = profileImageList.get(1);

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, user.getId(), 
						profileImage, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, user.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX, 
						profileThumbnailImage, new EntityCallback<String>() {

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
