package com.pinthecloud.item.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
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

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.interfaces.ItDialogCallback;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.view.CircleImageView;

public class ProfileSettingsFragment extends ItFragment {

	private final int GALLERY = 0;
	private final int CAMERA = 1;

	private Uri mProfileImageUri;
	private Bitmap mProfileImageBitmap;
	private CircleImageView mProfileImage;

	private TextView mId;
	private EditText mName;
	private EditText mDescription;
	private EditText mWebsite;

	private boolean mIsTypedNickName = true;
	private boolean mIsTakenProfileImage;


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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			String imagePath = null;
			switch(requestCode){
			case GALLERY:
				imagePath = getImagePathFromGallery(data);
				break;
			case CAMERA:
				imagePath = getImagePathFromCamera(data);
				break;
			}
			refineProfileImageBitmap(imagePath);
			mIsTakenProfileImage = true;
		}
	}


	private String getImagePathFromGallery(Intent data){
		mProfileImageUri = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = mActivity.getContentResolver().query(mProfileImageUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();

		return imagePath;
	}


	private String getImagePathFromCamera(Intent data){
		if(mProfileImageUri == null){
			if(data == null){
				mProfileImageUri = FileUtil.getLastCaptureBitmapUri(mActivity);
			} else{
				mProfileImageUri = data.getData();
				if(mProfileImageUri == null){
					// Intent pass data as Bitmap
					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
					mProfileImageUri = FileUtil.getOutputMediaFileUri();
					FileUtil.saveBitmapToFile(mActivity, mProfileImageUri, bitmap);
				}
			}
		}
		return mProfileImageUri.getPath();
	}


	private void refineProfileImageBitmap(String imagePath){
		mProfileImageBitmap = BitmapUtil.decodeInSampleSize(mActivity, mProfileImageUri, 
				BitmapUtil.BIG_PIC_SIZE, BitmapUtil.BIG_PIC_SIZE);

		int width = mProfileImageBitmap.getWidth();
		int height = mProfileImageBitmap.getHeight();
		if(height < width){
			mProfileImageBitmap = BitmapUtil.crop(mProfileImageBitmap, 0, 0, height, height);
		} else{
			mProfileImageBitmap = BitmapUtil.crop(mProfileImageBitmap, 0, 0, width, width);
		}

		int degree = BitmapUtil.getImageOrientation(imagePath);
		mProfileImageBitmap = BitmapUtil.rotate(mProfileImageBitmap, degree);
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
			Intent intent = new Intent(mActivity, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		mProfileImage = (CircleImageView)view.findViewById(R.id.profile_settings_frag_profile_image);
		mId = (TextView)view.findViewById(R.id.profile_settings_frag_id);
		mName = (EditText)view.findViewById(R.id.profile_settings_frag_nick_name);
		mDescription = (EditText)view.findViewById(R.id.profile_settings_frag_description);
		mWebsite = (EditText)view.findViewById(R.id.profile_settings_frag_website);
	}


	private void setComponent(){
		mName.addTextChangedListener(new TextWatcher() {

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
	}


	private void setProfileImage(){
		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getDialogItemList();
				ItDialogCallback[] callbacks = getDialogCallbacks(itemList);
				ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
				listDialog.show(getFragmentManager(), GlobalVariable.DIALOG_KEY);
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
				// Get image from gallery
				Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, GALLERY);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Get image from camera
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mProfileImageUri = FileUtil.getOutputMediaFileUri();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mProfileImageUri);
				startActivityForResult(intent, CAMERA);
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
