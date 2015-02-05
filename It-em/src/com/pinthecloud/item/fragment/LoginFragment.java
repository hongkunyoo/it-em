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
//import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class LoginFragment extends ItFragment {

	private com.facebook.widget.LoginButton mFacebookButton;
	private UiLifecycleHelper mFacebookUiHelper;
	
	private com.kakao.widget.LoginButton mKakaoButton;
    private SessionCallback mKakaoSessionCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFacebookUiHelper = new UiLifecycleHelper(mActivity, new StatusCallback() {

			@Override
			public void call(com.facebook.Session session, SessionState state, Exception exception) {
			}
		});
		
		mKakaoSessionCallback = new SessionCallback() {
			
	        @Override
	        public void onSessionOpened() {
	            // Login Success
//	        	kakaoLogin();
	        }

	        
	        @Override
	        public void onSessionClosed(final KakaoException exception) {
//	        	EventBus.getDefault().post(new ItException("onSessionClosed", ItException.TYPE.KAKAO_LOGIN_FAIL, exception));
	        	mKakaoButton.setVisibility(View.VISIBLE);
	        }
		};
		
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
		if(com.kakao.Session.initializeSession(mActivity, mKakaoSessionCallback)){
            // In Progress
			mKakaoButton.setVisibility(View.GONE);
        } else if (com.kakao.Session.getCurrentSession().isOpened()){
            // Already Opened
        	kakaoLogin();
        }
		
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
		mFacebookButton = (com.facebook.widget.LoginButton)view.findViewById(R.id.login_frag_facebook_button);
		mKakaoButton = (com.kakao.widget.LoginButton) view.findViewById(R.id.com_kakao_login);
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
		mFacebookButton.setUserInfoChangedCallback(new com.facebook.widget.LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				com.facebook.Session session = com.facebook.Session.getActiveSession();
				if (session != null && session.isOpened() || user != null) {
					facebookLogin(session, user);
				}
			}
		});
	}
	
	private void kakaoLogin() {
		UserManagement.requestMe(new MeResponseCallback() {

            @Override
            protected void onSuccess(final UserProfile userProfile) {
                
                String itUserId = String.valueOf(userProfile.getId());
        		final ItUser itUser = new ItUser(itUserId, ItUser.PLATFORM.KAKAO, itUserId,
        				userProfile.getNickname(), "", "", ItUser.TYPE.VIEWER);
        		itemLogin(itUser, userProfile.getProfileImagePath());
            }

            @Override
            protected void onNotSignedUp() {
            	EventBus.getDefault().post(new ItException("onNotSignedUp", ItException.TYPE.KAKAO_LOGIN_FAIL));
            }

            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
            	EventBus.getDefault().post(new ItException("onSessionClosedFailure", ItException.TYPE.KAKAO_LOGIN_FAIL, errorResult));
            }

            @Override
            protected void onFailure(final APIErrorResult errorResult) {
            	EventBus.getDefault().post(new ItException("onFailure", ItException.TYPE.KAKAO_LOGIN_FAIL, errorResult));
            }
        });
	}


	private void facebookLogin(com.facebook.Session session, final GraphUser user){

		String email = user.getProperty("email") == null ? user.getId() : user.getProperty("email").toString();
		final ItUser itUser = new ItUser(user.getId(), ItUser.PLATFORM.FACEBOOK, email,
				user.getFirstName().replace(" ", "_"), "", "", ItUser.TYPE.VIEWER);
		
		itemLogin(itUser, "https://graph.facebook.com/"+itUser.getItUserId()+"/picture?type=large");
	}
	
	private void itemLogin(final ItUser itUser, final String imageUrl) {
		mApp.showProgressDialog(mActivity);
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
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
				getProfileImageFromService(frag, imageUrl);
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
					AsyncChainer.notifyNext(frag);
				}
			});
		} else {
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
					goToNextActivity();
					AsyncChainer.clearChain(frag);
				}
			}
		});
	}


	private void getProfileImageFromService(final ItFragment frag, final String url){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap bitmap = null;
				try {
					bitmap = mApp.getPicasso()
							.load(url)
//							.resize(ImageUtil.PROFILE_IMAGE_SIZE, ImageUtil.PROFILE_IMAGE_SIZE)
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
