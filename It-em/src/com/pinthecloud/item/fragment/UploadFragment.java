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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.ItDialogCallback;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.util.MyLog;
import com.pinthecloud.item.view.SquareImageView;

public class UploadFragment extends ItFragment {

	public static final String MEDIA_KEY = "MEDIA_KEY";
	private int mMediaType;

	private Uri mImageUri;
	private Bitmap mImageBitmap;
	private Bitmap mSmallImageBitmap;

	private SquareImageView mImage;
	private EditText mContent;
	private Button mUploadButton;

	private boolean mIsTypedContent = false;
	private boolean mIsTakenImage;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = mActivity.getIntent();
		mMediaType = intent.getIntExtra(MEDIA_KEY, PrefHelper.DEFAULT_INT);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_upload, container, false);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();
		setImage();
		getMedia(mMediaType);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(!mIsTakenImage){
			mImage.setImageResource(R.drawable.ic_launcher);
		} else{
			mImage.setImageBitmap(mSmallImageBitmap);
		}
	}


	@Override
	public void onStop() {
		mImage.setImageBitmap(null);
		super.onStop();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			String imagePath = null;
			switch(requestCode){
			case FileUtil.GALLERY:
				imagePath = getImagePathFromGallery(data);
				break;
			case FileUtil.CAMERA:
				imagePath = getImagePathFromCamera(data);
				break;
			}
			refineImageBitmap(imagePath);

			mIsTakenImage = true;
			mUploadButton.setEnabled(isUploadButtonEnable());
		} else{
			mActivity.finish();
		}
	}


	private void getMedia(int mediaType){
		Intent intent = null;
		switch(mediaType){
		case FileUtil.GALLERY:
			intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(intent, FileUtil.GALLERY);
			break;
		case FileUtil.CAMERA:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mImageUri = FileUtil.getOutputMediaFileUri();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			startActivityForResult(intent, FileUtil.CAMERA);
			break;
		}
	}


	private String getImagePathFromGallery(Intent data){
		mImageUri = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = mActivity.getContentResolver().query(mImageUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();

		return imagePath;
	}


	private String getImagePathFromCamera(Intent data){
		if(mImageUri == null){
			if(data == null){
				mImageUri = FileUtil.getLastCaptureBitmapUri(mActivity);
			} else{
				mImageUri = data.getData();
				if(mImageUri == null){
					// Intent pass data as Bitmap
					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
					mImageUri = FileUtil.getOutputMediaFileUri();
					FileUtil.saveBitmapToFile(mActivity, mImageUri, bitmap);
				}
			}
		}
		return mImageUri.getPath();
	}


	private void refineImageBitmap(String imagePath){
		mImageBitmap = BitmapUtil.decodeInSampleSize(mActivity, mImageUri, 
				BitmapUtil.BIG_SIZE, BitmapUtil.BIG_SIZE);

		MyLog.log("width : " + mImageBitmap.getWidth());
		MyLog.log("height : " + mImageBitmap.getHeight());
		int width = mImageBitmap.getWidth();
		int height = mImageBitmap.getHeight();
		if(width >= height){
			mImageBitmap = BitmapUtil.crop(mImageBitmap, width/2 - height/2, 0, height, height);
		} else{
			mImageBitmap = BitmapUtil.crop(mImageBitmap, 0, height/2 - width/2, width, width);
		}

		int degree = BitmapUtil.getImageOrientation(imagePath);
		mImageBitmap = BitmapUtil.rotate(mImageBitmap, degree);
		mSmallImageBitmap = BitmapUtil.decodeInSampleSize(mImageBitmap, BitmapUtil.SMALL_SIZE, BitmapUtil.SMALL_SIZE);
	}	


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}


	private void findComponent(View view){
		mImage = (SquareImageView)view.findViewById(R.id.upload_frag_image);
		mContent = (EditText)view.findViewById(R.id.upload_frag_content);
		mUploadButton = (Button)view.findViewById(R.id.upload_frag_upload);
	}


	private void setComponent(){
		mContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					mIsTypedContent = false;
				}else{
					mIsTypedContent = true;
				}
				mUploadButton.setEnabled(isUploadButtonEnable());
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


	private void setButton(){
		mUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);

				Intent intent = new Intent(mActivity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}


	private void setImage(){
		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getDialogItemList();
				ItDialogCallback[] callbacks = getDialogCallbacks(itemList);
				ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
				listDialog.show(getFragmentManager(), ItDialogFragment.DIALOG_KEY);
			}
		});
	}


	private String[] getDialogItemList(){
		if(mIsTakenImage){
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
				getMedia(FileUtil.GALLERY);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				getMedia(FileUtil.CAMERA);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		if(mIsTakenImage){
			callbacks[2] = new ItDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Set profile image default
					mImage.setImageResource(R.drawable.ic_launcher);
					mIsTakenImage = false;
					mUploadButton.setEnabled(isUploadButtonEnable());
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
				}
			};
		}

		callbacks[itemList.length-1] = null;
		return callbacks;
	}


	private boolean isUploadButtonEnable(){
		return mIsTypedContent && mIsTakenImage;
	}
}
