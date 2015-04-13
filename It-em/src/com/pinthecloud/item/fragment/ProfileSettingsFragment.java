package com.pinthecloud.item.fragment;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.util.ImageUtil;

public class ProfileSettingsFragment extends ItFragment {

	private Uri mProfileImageUri;
	private ImageView mProfileImage;

	private EditText mNickName;
	private EditText mDescription;
	private EditText mWebsite;

	private ItUser mUser;
	private boolean isUpdating = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

		mGaHelper.sendScreen(mThisFragment);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		setProfileImage();
	}


	@Override
	public void onStop() {
		super.onStop();
		mProfileImage.setImageBitmap(null);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			String imagePath = null;

			switch(requestCode){
			case FileUtil.GALLERY:
				mProfileImageUri = data.getData();
				imagePath = FileUtil.getMediaPathFromGalleryUri(mActivity, mProfileImageUri);
				break;
			case FileUtil.CAMERA:
				mProfileImageUri = FileUtil.getMediaUriFromCamera(mActivity, data, mProfileImageUri);
				imagePath = mProfileImageUri.getPath();
				break;
			}

			getProfileImage(imagePath);
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.profile_settings, menu);
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.profile_settings_menu_submit);
		menuItem.setEnabled(mNickName.getText().toString().trim().length() > 0);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.profile_settings_menu_submit:
			if(isUpdating){
				break;
			}

			isUpdating = true;
			trimProfileSettings();

			if(!isProfileSettingsChanged()){
				isUpdating = false;
				mActivity.onBackPressed();
				break;
			}

			mApp.showProgressDialog(mActivity);

			AsyncChainer.asyncChain(mThisFragment, new Chainable(){

				@Override
				public void doNext(Object obj, Object... params) {
					String message = checkWebsite(obj, mWebsite.getText().toString());
					
					if(message.equals("")){
						message = checkNickName(obj, mNickName.getText().toString());	
					}
					
					if(message.equals("")){
						updateProfileSettings(obj);
					} else {
						AsyncChainer.notifyNext(obj, message, false);
					}
				}
			}, new Chainable(){

				@Override
				public void doNext(Object obj, Object... params) {
					String message = params[0].toString();
					boolean result = (Boolean)params[1];
					
					isUpdating = false;
					mApp.dismissProgressDialog();
					Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
					
					if(result){
						mObjectPrefHelper.put(mUser);
						mActivity.onBackPressed();
					}
				}
			});
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.profile_settings));
	}


	private void findComponent(View view){
		mProfileImage = (ImageView)view.findViewById(R.id.profile_settings_frag_profile_image);
		mNickName = (EditText)view.findViewById(R.id.profile_settings_frag_nick_name);
		mDescription = (EditText)view.findViewById(R.id.profile_settings_frag_description);
		mWebsite = (EditText)view.findViewById(R.id.profile_settings_frag_website);
	}


	private void setComponent(){
		mNickName.setText(mUser.getNickName());
		mNickName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mActivity.invalidateOptionsMenu();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mDescription.setText(mUser.getSelfIntro());
		mWebsite.setText(mUser.getWebPage());
	}


	private void setButton(){
		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getResources().getStringArray(R.array.profile_image_select_array);
				DialogCallback[] callbacks = getDialogCallbacks(itemList);

				ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
				listDialog.setCallbacks(callbacks);
				listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setProfileImage(){
		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(mUser.getId()))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(mProfileImage);
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];

		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				FileUtil.getMediaFromGallery(mThisFragment);
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing				
			}
			@Override
			public void doNegative(Bundle bundle) {
				// Do nothing
			}
		};

		callbacks[1] = new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				mProfileImageUri = FileUtil.getMediaFromCamera(mThisFragment);
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing				
			}
			@Override
			public void doNegative(Bundle bundle) {
				// Do nothing
			}
		};

		callbacks[2] = new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				// Set profile image default
				getProfileImage(R.drawable.profile_default_img);
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing				
			}
			@Override
			public void doNegative(Bundle bundle) {
				// Do nothing
			}
		};

		return callbacks;
	}


	private void trimProfileSettings(){
		mNickName.setText(mNickName.getText().toString().trim().replace(" ", "_").replace("\n", ""));
		mDescription.setText(mDescription.getText().toString().trim().replace("\n", " "));
		mWebsite.setText(mWebsite.getText().toString().trim().replace(" ", "").replace("\n", ""));
	}


	private boolean isProfileSettingsChanged(){
		return !mUser.getNickName().equals(mNickName.getText().toString())
				|| !mUser.getSelfIntro().equals(mDescription.getText().toString())
				|| !mUser.getWebPage().equals(mWebsite.getText().toString());
	}


	private String checkNickName(final Object obj, String nickName){
		String nickNameRegx = "^[a-zA-Z0-9가-힣_]+";
		int nickNameMinLength = getResources().getInteger(R.integer.nick_name_min_length);
		if(nickName.length() < nickNameMinLength){
			return getResources().getString(R.string.min_nick_name_message);
		} else if(!nickName.matches(nickNameRegx)){
			return getResources().getString(R.string.bad_nick_name_message);
		} else {
			return "";
		}
	}


	private String checkWebsite(final Object obj, String website){
		String websiteRegx = "^(https?\\://)?[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,5}(/\\S*)?$";
		if(website.equals("") || website.matches(websiteRegx)){
			return "";
		} else {
			return getResources().getString(R.string.bad_website_message);
		}
	}


	private void updateProfileSettings(final Object obj){
		mUser.setNickName(mNickName.getText().toString());
		mUser.setSelfIntro(mDescription.getText().toString());
		mUser.setWebPage(mWebsite.getText().toString());
		
		mUserHelper.updateUser(mUser, new EntityCallback<Integer>() {

			@Override
			public void onCompleted(Integer statusCode) {
				if(!isAdded()){
					return;
				}
				
				if(statusCode == HttpURLConnection.HTTP_OK){
					AsyncChainer.notifyNext(obj, getResources().getString(R.string.profile_edited), true);
				} else if(statusCode == HttpURLConnection.HTTP_CONFLICT) {
					AsyncChainer.notifyNext(obj, getResources().getString(R.string.duplicated_nick_name_message), false);
				} else {
					AsyncChainer.notifyNext(obj, getResources().getString(R.string.error_message), false);
				}
			}
		});
	}


	private void getProfileImage(final String imagePath){
		(new AsyncTask<Void,Void,List<Bitmap>>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mApp.showProgressDialog(mActivity);
			}

			@Override
			protected List<Bitmap> doInBackground(Void... params) {
				Bitmap profileImage = ImageUtil.refineSquareImage(imagePath, ImageUtil.PROFILE_IMAGE_SIZE, true);
				Bitmap profileThumbnailImage = ImageUtil.refineSquareImage(imagePath, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE, true);

				List<Bitmap> profileImageList = new ArrayList<Bitmap>();
				profileImageList.add(profileImage);
				profileImageList.add(profileThumbnailImage);
				return profileImageList;
			}

			@Override
			protected void onPostExecute(List<Bitmap> profileImageList) {
				Bitmap profileImage = profileImageList.get(0);
				Bitmap profileThumbnailImage = profileImageList.get(1);
				updateProfileImage(profileImage, profileThumbnailImage);
			};
		}).execute();
	}


	private void getProfileImage(final int resId){
		(new AsyncTask<Void,Void,List<Bitmap>>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mApp.showProgressDialog(mActivity);
			}

			@Override
			protected List<Bitmap> doInBackground(Void... params) {
				Bitmap profileImage = ImageUtil.refineSquareImage(getResources(), resId, ImageUtil.PROFILE_IMAGE_SIZE, true);
				Bitmap profileThumbnailImage = ImageUtil.refineSquareImage(getResources(), resId, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE, true);

				List<Bitmap> profileImageList = new ArrayList<Bitmap>();
				profileImageList.add(profileImage);
				profileImageList.add(profileThumbnailImage);
				return profileImageList;
			}

			@Override
			protected void onPostExecute(List<Bitmap> profileImageList) {
				Bitmap profileImage = profileImageList.get(0);
				Bitmap profileThumbnailImage = profileImageList.get(1);
				updateProfileImage(profileImage, profileThumbnailImage);
			};
		}).execute();
	}


	private void updateProfileImage(final Bitmap profileImageBitmap, final Bitmap profileThumbnailImageBitmap){
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(2);

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, mUser.getId(), 
						profileImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, mUser.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX,
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
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.profile_image_edited), Toast.LENGTH_LONG).show();

				FileUtil.clearCache();
				setProfileImage();
			}
		});
	}
}
