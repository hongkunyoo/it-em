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
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.interfaces.ItDialogCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.view.CircleImageView;

public class ProfileSettingsFragment extends ItFragment {

	private Uri mProfileImageUri;
	private Bitmap mProfileImageBitmap;
	private Bitmap mSmallProfileImageBitmap;
	private CircleImageView mProfileImage;

	private TextView mId;
	private EditText mNickName;
	private EditText mDescription;
	private EditText mWebsite;

	private ItUser me;
	private boolean mIsTypedNickName = true;
	private boolean mIsTakenProfileImage;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = mObjectPrefHelper.get(ItUser.class);
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
		setProfileImage();
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(!mIsTakenProfileImage){
			mProfileImage.setImageResource(R.drawable.ic_launcher);
		} else{
			mProfileImage.setImageBitmap(mSmallProfileImageBitmap);
		}
	}


	@Override
	public void onStop() {
		mProfileImage.setImageBitmap(null);
		super.onStop();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			String imagePath = FileUtil.getMediaPath(mActivity, data, mProfileImageUri, requestCode);
			mProfileImageBitmap = BitmapUtil.refineImageBitmap(mActivity, imagePath);
			mSmallProfileImageBitmap = BitmapUtil.decodeInSampleSize(mProfileImageBitmap, BitmapUtil.SMALL_SIZE, BitmapUtil.SMALL_SIZE);
			mIsTakenProfileImage = true;
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.profile_settings, menu);
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.profile_settings_done);
		menuItem.setEnabled(mIsTypedNickName);
		super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.profile_settings_done:
			setProfile();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setProfile(){
		mApp.showProgressDialog(mActivity);

		Intent intent = new Intent(mActivity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	private void findComponent(View view){
		mProfileImage = (CircleImageView)view.findViewById(R.id.profile_settings_frag_profile_image);
		mId = (TextView)view.findViewById(R.id.profile_settings_frag_id);
		mNickName = (EditText)view.findViewById(R.id.profile_settings_frag_nick_name);
		mDescription = (EditText)view.findViewById(R.id.profile_settings_frag_description);
		mWebsite = (EditText)view.findViewById(R.id.profile_settings_frag_website);
	}


	private void setComponent(){
		mNickName.setText(me.getNickName());
		mNickName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					mIsTypedNickName = false;
				}else{
					mIsTypedNickName = true;
				}
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

		mId.setText(me.getItUserId());
		mDescription.setText(me.getSelfIntro());
		mWebsite.setText(me.getWebPage());
	}


	private void setProfileImage(){
		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getDialogItemList();
				ItDialogCallback[] callbacks = getDialogCallbacks(itemList);
				ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
				listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private String[] getDialogItemList(){
		if(mIsTakenProfileImage){
			return getResources().getStringArray(R.array.image_select_delete_string_array);
		}else{
			return getResources().getStringArray(R.array.image_select_string_array);
		}
	}


	private ItDialogCallback[] getDialogCallbacks(String[] itemList){
		ItDialogCallback[] callbacks = new ItDialogCallback[itemList.length];

		callbacks[0] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mProfileImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.GALLERY);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mProfileImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.CAMERA);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		if(mIsTakenProfileImage){
			callbacks[2] = new ItDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Set profile image default
					mProfileImage.setImageResource(R.drawable.ic_launcher);
					mIsTakenProfileImage = false;
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
				}
			};
		}

		callbacks[itemList.length-1] = null;
		return callbacks;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
