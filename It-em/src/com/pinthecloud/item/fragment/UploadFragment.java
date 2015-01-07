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
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.view.SquareImageView;

public class UploadFragment extends ItFragment {

	private Uri mImageUri;
	private Bitmap mImageBitmap;
	private Bitmap mSmallImageBitmap;

	private SquareImageView mImage;
	private EditText mContent;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_upload, container, false);

		setHasOptionsMenu(true);
		findComponent(view);
		setComponent();
		setImage();

		mImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.GALLERY);

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(mImageBitmap == null){
			mImage.setImageResource(R.drawable.launcher);
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
			mActivity.invalidateOptionsMenu();
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
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.upload_submit);
		menuItem.setEnabled(isSubmitEnable());
		super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.upload_submit:
			ItUser myItUser = mObjectPrefHelper.get(ItUser.class);
			Item item = new Item(mContent.getText().toString(), myItUser.getNickName(), myItUser.getId());
			uploadItem(item);
			break;
		}
		return super.onOptionsItemSelected(menuItem);
	}


	private void findComponent(View view){
		mImage = (SquareImageView)view.findViewById(R.id.upload_frag_image);
		mContent = (EditText)view.findViewById(R.id.upload_frag_content);
	}


	private void setComponent(){
		mContent.addTextChangedListener(new TextWatcher() {

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
	}


	private void setImage(){
		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getDialogItemList();
				DialogCallback[] callbacks = getDialogCallbacks(itemList);
				ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
				listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private String[] getDialogItemList(){
		if(mImageBitmap != null){
			return getResources().getStringArray(R.array.upload_image_select_delete_string_array);
		}else{
			return getResources().getStringArray(R.array.upload_image_select_string_array);
		}
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];

		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				mImageUri = FileUtil.getMediaUri(mThisFragment, FileUtil.GALLERY);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		if(mImageBitmap != null){
			callbacks[1] = new DialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Set profile image default
					mImage.setImageResource(R.drawable.launcher);
					mImageBitmap = null;
					mActivity.invalidateOptionsMenu();
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
				}
			};
		}

		return callbacks;
	}


	private void uploadItem(final Item item){
		mApp.showProgressDialog(mActivity);
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				mAimHelper.add(frag, item, new EntityCallback<Item>() {

					@Override
					public void onCompleted(Item entity) {
						item.setId(entity.getId());
						item.setRawCreateDateTime(entity.getRawCreateDateTime());
						AsyncChainer.notifyNext(frag);
					}
				});
			}
			
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.ITEM_IMAGE, item.getId(), mImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						mApp.dismissProgressDialog();
						Toast.makeText(mActivity, getResources().getString(R.string.uploaded), Toast.LENGTH_LONG).show();

						Intent intent = new Intent();
						intent.putExtra(Item.INTENT_KEY, item);
						mActivity.setResult(Activity.RESULT_OK, intent);
						mActivity.finish();
					}
				});
			}
		});
	}


	private boolean isSubmitEnable(){
		return mContent.getText().toString().trim().length() > 0 && mImageBitmap != null;
	}
}
