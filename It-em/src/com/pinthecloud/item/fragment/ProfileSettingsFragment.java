package com.pinthecloud.item.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class ProfileSettingsFragment extends ItFragment {

	private Uri mProfileImageUri;
	private CircleImageView mProfileImage;

	private EditText mNickName;
	private EditText mDescription;
	private EditText mWebsite;

	private ItUser mMyItUser;

	private boolean mIsUpdating = false;


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
		setActionBar();
		findComponent(view);
		setComponent();
		setProfileImageEvent();
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
		switch(requestCode){
		case FileUtil.GALLERY | FileUtil.CAMERA:
			if (resultCode == Activity.RESULT_OK){
				String imagePath = FileUtil.getMediaPath(mActivity, data, mProfileImageUri, requestCode);
				Bitmap profileImageBitmap = BitmapUtil.refineImageBitmap(mActivity, imagePath);
				updateProfileImage(profileImageBitmap);
			}
			break;
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
		MenuItem menuItem = menu.findItem(R.id.profile_settings_done);
		menuItem.setEnabled(mNickName.getText().toString().trim().length() > 0);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.profile_settings_done:
			if(!mIsUpdating){
				mIsUpdating = true;
				trimProfileSettings();
				if(isProfileSettingsChanged()){
					mApp.showProgressDialog(mActivity);
					AsyncChainer.asyncChain(mThisFragment, new Chainable(){

						@Override
						public void doNext(final ItFragment frag, Object... params) {
							checkNickName(frag, mNickName.getText().toString());
						}
					}, new Chainable(){

						@Override
						public void doNext(ItFragment frag, Object... params) {
							String message = params[0].toString();
							if(message.equals("")){
								updateProfileSettings();
							} else {
								mIsUpdating = false;
								mApp.dismissProgressDialog();
								Toast.makeText(mActivity, params[0].toString(), Toast.LENGTH_LONG).show();	
							}
						}
					});
				} else {
					mActivity.onBackPressed();
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}


	private void findComponent(View view){
		mProfileImage = (CircleImageView)view.findViewById(R.id.profile_settings_frag_profile_image);
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


	private void setProfileImage(){
		Picasso.with(mProfileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(mMyItUser.getId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(mProfileImage);
	}


	private void setProfileImageEvent(){
		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getResources().getStringArray(R.array.profile_image_select_string_array);
				DialogCallback[] callbacks = getDialogCallbacks(itemList);
				ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
				listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];

		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mProfileImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.GALLERY);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mProfileImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.CAMERA);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[2] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Set profile image default
				Bitmap profileImageBitmap = BitmapUtil.decodeInSampleSize(getResources(), R.drawable.launcher, BitmapUtil.BIG_SIZE, BitmapUtil.BIG_SIZE);
				updateProfileImage(profileImageBitmap);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		return callbacks;
	}


	private void trimProfileSettings(){
		mNickName.setText(mNickName.getText().toString().trim());
		mDescription.setText(mDescription.getText().toString().trim());
		mWebsite.setText(mWebsite.getText().toString());
	}


	private boolean isProfileSettingsChanged(){
		return !mMyItUser.getNickName().equals(mNickName.getText().toString())
				|| !mMyItUser.getSelfIntro().equals(mDescription.getText().toString())
				|| !mMyItUser.getWebPage().equals(mWebsite.getText().toString());
	}



	private void checkNickName(final ItFragment frag, String nickName){
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,10}$";
		if(nickName.length() < 2){
			AsyncChainer.notifyNext(frag, getResources().getString(R.string.min_nick_name_message));
		} else if(!nickName.matches(nickNameRegx)){
			AsyncChainer.notifyNext(frag, getResources().getString(R.string.bad_nick_name_message));
		} else {
			mUserHelper.getByNickName(mThisFragment, nickName, new EntityCallback<ItUser>() {

				@Override
				public void onCompleted(ItUser entity) {
					if(entity == null){
						AsyncChainer.notifyNext(frag, "");
					} else {
						if(entity.getId().equals(mMyItUser.getId())){
							AsyncChainer.notifyNext(frag, "");
						} else {
							AsyncChainer.notifyNext(frag, getResources().getString(R.string.duplicated_nick_name_message));
						}
					}
				}
			});
		}
	}


	private void updateProfileImage(final Bitmap profileImageBitmap){
		mApp.showProgressDialog(mActivity);
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(2);

				mBlobStorageHelper.uploadBitmapAsync(mThisFragment, BlobStorageHelper.USER_PROFILE, mMyItUser.getId(), 
						profileImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});

				Bitmap smallProfileImageBitmap = BitmapUtil.decodeInSampleSize(profileImageBitmap, BitmapUtil.SMALL_SIZE, BitmapUtil.SMALL_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(mThisFragment, BlobStorageHelper.USER_PROFILE, mMyItUser.getId()+BitmapUtil.SMALL_POSTFIX,
						smallProfileImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});
			}

		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				FileUtil.clearCache();
				setProfileImage();

				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.profile_image_edited), Toast.LENGTH_LONG).show();
			}
		});
	}


	private void updateProfileSettings(){
		mMyItUser.setNickName(mNickName.getText().toString());
		mMyItUser.setSelfIntro(mDescription.getText().toString().trim());
		mMyItUser.setWebPage(mWebsite.getText().toString());

		mUserHelper.update(mThisFragment, mMyItUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mObjectPrefHelper.put(entity);

				mIsUpdating = false;
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.profile_edited), Toast.LENGTH_LONG).show();

				Intent intent = new Intent();
				intent.putExtra(ItUser.INTENT_KEY, entity);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();
			}
		});
	}
}
