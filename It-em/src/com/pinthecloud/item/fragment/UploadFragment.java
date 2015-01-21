package com.pinthecloud.item.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.util.ImageUtil;

public class UploadFragment extends ItFragment {

	private TextView mTitle;
	private ImageButton mSubmit;

	private Uri mItemImageUri;

	private ImageView mItemImage;
	private EditText mContent;


	public static ItFragment newInstance(Uri itemImageUri) {
		ItFragment fragment = new UploadFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, itemImageUri);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItemImageUri = getArguments().getParcelable(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_upload, container, false);

		setHasOptionsMenu(true);
		setToolbar(inflater);
		findComponent(view);
		setComponent();
		setButton();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		mApp.getPicasso()
		.load(mItemImageUri)
		.placeholder(R.drawable.launcher)
		.fit().centerCrop()
		.into(mItemImage);
	}


	@Override
	public void onStop() {
		super.onStop();
		mItemImage.setImageBitmap(null);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case FileUtil.GALLERY:
			if (resultCode == Activity.RESULT_OK){
				mItemImageUri = data.getData();
				mSubmit.setEnabled(isSubmitEnable());
			}
			break;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(menuItem);
	}


	private void setToolbar(LayoutInflater inflater){
		Toolbar toolbar = mActivity.getToolbar();
		View view = inflater.inflate(R.layout.custom_toolbar_submit_view, toolbar, false);
		mTitle = (TextView)view.findViewById(R.id.custom_toolbar_submit_title);
		mSubmit = (ImageButton)view.findViewById(R.id.custom_toolbar_submit_button);
		toolbar.addView(view);
	}


	private void findComponent(View view){
		mItemImage = (ImageView)view.findViewById(R.id.upload_frag_item_image);
		mContent = (EditText)view.findViewById(R.id.upload_frag_content);
	}


	private void setComponent(){
		mTitle.setText(getResources().getString(R.string.upload));

		mContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mSubmit.setEnabled(isSubmitEnable());
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
		mSubmit.setEnabled(isSubmitEnable());
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadItem();
			}
		});

		mItemImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = getDialogItemList();
				DialogCallback[] callbacks = getDialogCallbacks(itemList);

				ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
				listDialog.setCallbacks(callbacks);
				listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private String[] getDialogItemList(){
		if(mItemImageUri != null){
			return getResources().getStringArray(R.array.upload_image_select_delete_string_array);
		}else{
			return getResources().getStringArray(R.array.upload_image_select_string_array);
		}
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];

		for(int i=0 ; i<itemList.length ; i++){
			switch(i){
			case 0:
				callbacks[0] = new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						FileUtil.getMediaFromGallery(mThisFragment);
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
					}
				};
				break;
			case 1:
				callbacks[1] = new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						// Set profile image default
						mItemImageUri = null;
						mItemImage.setImageResource(R.drawable.launcher);
						mSubmit.setEnabled(isSubmitEnable());
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
					}
				};
				break;
			}
		}

		return callbacks;
	}


	private void uploadItem(){
		mApp.showProgressDialog(mActivity);

		ItUser myItUser = mObjectPrefHelper.get(ItUser.class);
		final String itemImagePath = FileUtil.getMediaPathFromGalleryUri(mActivity, mItemImageUri);
		final Bitmap itemImageBitmap = ImageUtil.refineItemImage(itemImagePath, ImageUtil.ITEM_IMAGE_WIDTH);
		final Item item = new Item(mContent.getText().toString(), myItUser.getNickName(), myItUser.getId(),
				itemImageBitmap.getWidth(), itemImageBitmap.getHeight());

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				mAimHelper.add(item, new EntityCallback<Item>() {

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
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(3);

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId(),
						itemImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});

				Bitmap itemPreviewImageBitmap = ImageUtil.refineItemImage(itemImagePath, ImageUtil.ITEM_PREVIEW_IMAGE_WIDTH);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX,
						itemPreviewImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});

				Bitmap itemThumbnailImageBitmap = ImageUtil.refineSquareImage(itemImagePath, ImageUtil.ITEM_THUMBNAIL_IMAGE_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX,
						itemThumbnailImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(frag);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.uploaded), Toast.LENGTH_LONG).show();

				Intent intent = new Intent();
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();
			}
		});
	}


	private boolean isSubmitEnable(){
		return mContent.getText().toString().trim().length() > 0 && mItemImageUri != null;
	}
}
