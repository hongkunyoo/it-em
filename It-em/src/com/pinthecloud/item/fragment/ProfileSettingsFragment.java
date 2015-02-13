package com.pinthecloud.item.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

	private ItUser mMyItUser;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

		setHasOptionsMenu(true);
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

			updateProfileImage(imagePath);
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
		MenuItem menuItem = menu.findItem(R.id.profile_settings_submit);
		menuItem.setEnabled(mNickName.getText().toString().length() > 0);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.profile_settings_submit:
			trimProfileSettings();
			if(isProfileSettingsChanged()){
				mApp.showProgressDialog(mActivity);

				AsyncChainer.asyncChain(mThisFragment, new Chainable(){

					@Override
					public void doNext(Object obj, Object... params) {
						checkNickName(obj, mNickName.getText().toString());
					}
				}, new Chainable(){

					@Override
					public void doNext(Object obj, Object... params) {
						String message = params[0].toString();
						if(message.equals("")){
							updateProfileSettings();
						} else {
							mApp.dismissProgressDialog();
							Toast.makeText(mActivity, params[0].toString(), Toast.LENGTH_LONG).show();	
						}
					}
				});
			} else {
				mActivity.onBackPressed();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		mProfileImage = (ImageView)view.findViewById(R.id.profile_settings_frag_profile_image);
		mNickName = (EditText)view.findViewById(R.id.profile_settings_frag_nick_name);
		mDescription = (EditText)view.findViewById(R.id.profile_settings_frag_description);
		mWebsite = (EditText)view.findViewById(R.id.profile_settings_frag_website);
	}


	private void setComponent(){
		mNickName.setText(mMyItUser.getNickName());
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

		mDescription.setText(mMyItUser.getSelfIntro());
		mWebsite.setText(mMyItUser.getWebPage());
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
		.load(BlobStorageHelper.getUserProfileImgUrl(mMyItUser.getId()))
		.placeholder(R.drawable.profile_l_default_img)
		.fit()
		.into(mProfileImage);
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];

		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				FileUtil.getMediaFromGallery(mThisFragment);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mProfileImageUri = FileUtil.getMediaFromCamera(mThisFragment);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[2] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Set profile image default
				updateProfileImage(R.drawable.profile_s_default_img);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		return callbacks;
	}


	private void trimProfileSettings(){
		mNickName.setText(mNickName.getText().toString().trim().replace(" ", "").replace("\n", ""));
		mDescription.setText(mDescription.getText().toString().trim().replace("\n", " "));
		mWebsite.setText(mWebsite.getText().toString().trim().replace(" ", "").replace("\n", ""));
	}


	private boolean isProfileSettingsChanged(){
		return !mMyItUser.getNickName().equals(mNickName.getText().toString())
				|| !mMyItUser.getSelfIntro().equals(mDescription.getText().toString())
				|| !mMyItUser.getWebPage().equals(mWebsite.getText().toString());
	}


	private void checkNickName(final Object obj, String nickName){
		int nickNameMinLength = getResources().getInteger(R.integer.nick_name_min_length);
		int nickNameMaxLength = getResources().getInteger(R.integer.nick_name_max_length);
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{" + nickNameMinLength + "," + nickNameMaxLength + "}$";
		
		if(nickName.length() < nickNameMinLength){
			AsyncChainer.notifyNext(obj, getResources().getString(R.string.min_nick_name_message));
		} else if(!nickName.matches(nickNameRegx)){
			AsyncChainer.notifyNext(obj, getResources().getString(R.string.bad_nick_name_message));
		} else {
			mUserHelper.getByNickName(nickName, new EntityCallback<ItUser>() {

				@Override
				public void onCompleted(ItUser entity) {
					if(entity == null){
						AsyncChainer.notifyNext(obj, "");
					} else {
						if(entity.getId().equals(mMyItUser.getId())){
							AsyncChainer.notifyNext(obj, "");
						} else {
							AsyncChainer.notifyNext(obj, getResources().getString(R.string.duplicated_nick_name_message));
						}
					}
				}
			});
		}
	}

	
	private void updateProfileSettings(){
		mMyItUser.setNickName(mNickName.getText().toString());
		mMyItUser.setSelfIntro(mDescription.getText().toString());
		mMyItUser.setWebPage(mWebsite.getText().toString());
		
		mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.profile_edited), Toast.LENGTH_LONG).show();

				mObjectPrefHelper.put(entity);

				Intent intent = new Intent();
				intent.putExtra(ItUser.INTENT_KEY, entity);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();
			}
		});
	}


	private void updateProfileImage(String imagePath){
		mApp.showProgressDialog(mActivity);
		Bitmap profileImageBitmap = ImageUtil.refineSquareImage(imagePath, ImageUtil.PROFILE_IMAGE_SIZE);
		Bitmap profileThumbnailImageBitmap = ImageUtil.refineSquareImage(imagePath, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE);
		updateProfileImage(profileImageBitmap, profileThumbnailImageBitmap);
	}


	private void updateProfileImage(int resId){
		mApp.showProgressDialog(mActivity);
		Bitmap profileImageBitmap = ImageUtil.refineSquareImage(getResources(), resId, ImageUtil.PROFILE_IMAGE_SIZE);
		Bitmap profileThumbnailImageBitmap = ImageUtil.refineSquareImage(getResources(), resId, ImageUtil.PROFILE_THUMBNAIL_IMAGE_SIZE);
		updateProfileImage(profileImageBitmap, profileThumbnailImageBitmap);
	}


	private void updateProfileImage(final Bitmap profileImageBitmap, final Bitmap profileThumbnailImageBitmap){
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(2);

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, mMyItUser.getId(), 
						profileImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_USER_PROFILE, mMyItUser.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX,
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
