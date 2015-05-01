package com.pinthecloud.item.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class LoginFragment extends ItFragment {

	private Button mFacebookButton;
	private CallbackManager mFacebookCallbackManager;

	private com.kakao.Session mKakaoSession;
	private SessionCallback mKakaoSessionCallback;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Facebook
		mFacebookCallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult loginResult) {
				facebookLogin(loginResult);
			}
			@Override
			public void onCancel() {
				// Do nothing
			}
			@Override
			public void onError(FacebookException exception) {
				Toast.makeText(mActivity, getResources().getString(R.string.error_message), Toast.LENGTH_LONG).show();
			}
		});

		// Kakao
		mKakaoSessionCallback = new SessionCallback() {

			@Override
			public void onSessionOpening() {
				// Do nothing
			}
			@Override
			public void onSessionOpened() {
				kakaoLogin();
			}
			@Override
			public void onSessionClosed(KakaoException exception) {
				Toast.makeText(mActivity, getResources().getString(R.string.error_message), Toast.LENGTH_LONG).show();
			}
		};

		mKakaoSession = com.kakao.Session.getCurrentSession();
		mKakaoSession.addCallback(mKakaoSessionCallback);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		mGaHelper.sendScreen(mThisFragment);
		findComponent(view);
		setButton();

		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		if(!mKakaoSession.isClosed() && mKakaoSession.isOpenable()) {
			mKakaoSession.implicitOpen();
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (com.kakao.Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
		mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		mKakaoSession.removeCallback(mKakaoSessionCallback);
	}


	private void findComponent(View view){
		mFacebookButton = (Button)view.findViewById(R.id.login_facebook);
	}


	private void setButton(){
		mFacebookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile"));
			}
		});
	}


	private void facebookLogin(LoginResult loginResult){
		GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
				new GraphRequest.GraphJSONObjectCallback() {

			@Override
			public void onCompleted(JSONObject object, GraphResponse response) {
				try {
					String id = object.getString("id");
					String firstName = object.getString("first_name");
					ItUser user = new ItUser(id, ItUser.PLATFORM.FACEBOOK, firstName.replace(" ", "_"), ItUser.TYPE.VIEWER);
					itemLogin(user, "https://graph.facebook.com/"+user.getItUserId()+"/picture?type=large");
				} catch (JSONException e) {
					// Do nothing
				}
			}
		});

		Bundle parameters = new Bundle();
		parameters.putString("fields", "id, first_name");
		request.setParameters(parameters);
		request.executeAsync();
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
		mApp.showProgressDialog(mActivity);
		AsyncChainer.asyncChain(mActivity, new Chainable(){

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
				mBlobStorageHelper.isExistAsync(BlobStorageHelper.getUserProfileContainer(), user.getId(), new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						if(entity){
							gotoNextActivity();
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
					if(url == null) throw new IOException();
					Bitmap profileImage = mApp.getPicasso().load(url).get();
					if(profileImage == null) throw new IOException();
					return profileImage;
				} catch (IOException e) {
					return BitmapFactory.decodeResource(getResources(), R.drawable.profile_default_img);
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
		AsyncChainer.asyncChain(mActivity, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(2);

				Bitmap profileImage = profileImageList.get(0);
				Bitmap profileThumbnailImage = profileImageList.get(1);

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.getUserProfileContainer(), user.getId(), 
						profileImage, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.getUserProfileContainer(), user.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX, 
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
				gotoNextActivity();
			}
		});
	}


	private void gotoNextActivity(){
		mApp.dismissProgressDialog();
		Intent intent = new Intent(mActivity, MainActivity.class);
		startActivity(intent);
		mActivity.finish();
	}
}
