package com.pinthecloud.item.fragment;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.squareup.picasso.Picasso;

public class LoginFragment extends ItFragment {

	private LoginButton mFacebookButton;
	private UiLifecycleHelper mUiHelper;
	private Button mTempButton;

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
		mTempButton = (Button)view.findViewById(R.id.login_frag_temp_button);
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
		
		mTempButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				goToNextActivity();
			}
		});
	}


	private void facebookLogin(final GraphUser user){
		final ItUser itUser = new ItUser();
		itUser.setItUserId(user.getId());
		itUser.setNickName(user.getFirstName());
		itUser.setSelfIntro("");
		// e.g. https://athere.blob.core.windows.net/userprofile/ID
		itUser.setWebPage("");
		
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				// TODO Auto-generated method stub
				userHelper.add(frag, itUser, new ItEntityCallback<ItUser>() {

					@Override
					public void onCompleted(ItUser entity) {
						// TODO Auto-generated method stub
						mObjectPrefHelper.put(entity);
						itUser.setId(entity.getId());
						goToNextActivity();
					}
				});
			}
			
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				// TODO Auto-generated method stub
				
				new AsyncTask<Void,Void,Bitmap>(){

					@Override
					protected Bitmap doInBackground(Void... params) {
						// TODO Auto-generated method stub
						Bitmap bm = null;
						try {
							bm = Picasso.with(mActivity).load("https://graph.facebook.com/"+user.getId()+"/picture?type=large").get();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return bm;
					}
					
					protected void onPostExecute(Bitmap result) {
						AsyncChainer.notifyNext(frag, (Object)result);
					};
					
				}.execute();
				
				
			}
			
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				// TODO Auto-generated method stub
				Bitmap picture = (Bitmap)params[0];
				blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, itUser.getId(), picture, new ItEntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						// TODO Auto-generated method stub
						AsyncChainer.notifyNext(frag);
					}
				});
			}
			
		});
	}


	private void goToNextActivity(){
		Intent intent = new Intent(mActivity, MainActivity.class);
		startActivity(intent);
		mActivity.finish();
	}


	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	}
}
