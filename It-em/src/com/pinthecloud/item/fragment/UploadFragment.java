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
import android.widget.Button;
import android.widget.EditText;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.ItDialogCallback;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
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
		findComponent(view);
		setComponent();
		setButton();
		setImage();
		mImageUri = FileUtil.getMediaUri(mThisFragment, mMediaType);
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
			String imagePath = FileUtil.getMediaPath(mActivity, data, mImageUri, requestCode);
			mImageBitmap = BitmapUtil.refineImageBitmap(mActivity, imagePath);
			mSmallImageBitmap = BitmapUtil.decodeInSampleSize(mImageBitmap, BitmapUtil.SMALL_SIZE, BitmapUtil.SMALL_SIZE);

			mIsTakenImage = true;
			mUploadButton.setEnabled(isUploadButtonEnable());
		} else{
			mActivity.finish();
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.upload, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.upload_close:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
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

				ItUser me = mObjectPrefHelper.get(ItUser.class);
				final Item item = new Item();
				item.setContent(mContent.getText().toString());
				item.setWhoMade(me.getNickName());
				item.setWhoMadeId(me.getId());

				AsyncChainer.asyncChain(mThisFragment, new Chainable(){

					@Override
					public void doNext(final ItFragment frag, Object... params) {
						mAimHelper.add(frag, item, new ItEntityCallback<Item>() {

							@Override
							public void onCompleted(Item entity) {
								item.setId(entity.getId());
								AsyncChainer.notifyNext(frag);
							}
						});
					}

				}, new Chainable(){

					@Override
					public void doNext(ItFragment frag, Object... params) {
						blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.ITEM_IMAGE, item.getId(), mImageBitmap, new ItEntityCallback<String>() {

							@Override
							public void onCompleted(String entity) {
								mApp.dismissProgressDialog();
								Intent intent = new Intent(mActivity, MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						});
					}
				});
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
				listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
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
				mImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.GALLERY);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.CAMERA);
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
