package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.BrandInformationListAdapter;
import com.pinthecloud.item.dialog.CategoryDialog;
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

	public static final String CATEGORY_INTENT_KEY = "CATEGORY_INTENT_KEY";
	
	private Uri mItemImageUri;

	private ImageView mItemImage;
	private EditText mContent;

	private Button mBrandInformationAdd;
	private RecyclerView mListView;
	private BrandInformationListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<BrandInformation> mBrandInformationList;

	private ItUser mMyItUser;


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
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
		mItemImageUri = getArguments().getParcelable(Item.INTENT_KEY);
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
		setList();
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		mApp.getPicasso()
		.load(mItemImageUri)
		.placeholder(R.drawable.upload_thumbnail_default_img)
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
				mActivity.invalidateOptionsMenu();
			}
			break;
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.upload, menu);
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.upload_submit);
		menuItem.setEnabled(isSubmitEnable());
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.upload_submit:
			trimContent();
			uploadItem();
			break;
		}
		return super.onOptionsItemSelected(menuItem);
	}


	private void findComponent(View view){
		mItemImage = (ImageView)view.findViewById(R.id.upload_frag_item_image);
		mContent = (EditText)view.findViewById(R.id.upload_frag_content);
		mBrandInformationAdd = (Button)view.findViewById(R.id.upload_frag_brand_information_add);
		mListView = (RecyclerView)view.findViewById(R.id.upload_frag_brand_information_list);
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


	private void setButton(){
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

		mBrandInformationAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CategoryDialog categoryDialog = new CategoryDialog();
				categoryDialog.setCallback(new DialogCallback() {
					
					@Override
					public void doPositiveThing(Bundle bundle) {
						String category = bundle.getString(CATEGORY_INTENT_KEY);
						mListAdapter.add(mBrandInformationList.size(), new BrandInformation(category));
						categoryDialog.dismiss();
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
					}
				});
				categoryDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mBrandInformationList = new ArrayList<BrandInformation>();
		mListAdapter = new BrandInformationListAdapter(mBrandInformationList);
		mListView.setAdapter(mListAdapter);
	}


	private String[] getDialogItemList(){
		if(mItemImageUri != null){
			return getResources().getStringArray(R.array.upload_image_select_delete_array);
		}else{
			return getResources().getStringArray(R.array.upload_image_select_array);
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
						mItemImage.setImageResource(R.drawable.upload_thumbnail_default_img);
						mActivity.invalidateOptionsMenu();
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

		final String itemImagePath = FileUtil.getMediaPathFromGalleryUri(mActivity, mItemImageUri);
		final Bitmap itemImageBitmap = ImageUtil.refineItemImage(itemImagePath, ImageUtil.ITEM_IMAGE_WIDTH);
		final Item item = new Item(mContent.getText().toString(), mMyItUser.getNickName(), mMyItUser.getId(),
				itemImageBitmap.getWidth(), itemImageBitmap.getHeight());

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				mAimHelper.add(item, new EntityCallback<Item>() {

					@Override
					public void onCompleted(Item entity) {
						item.setId(entity.getId());
						item.setRawCreateDateTime(entity.getRawCreateDateTime());
						AsyncChainer.notifyNext(obj);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(3);

				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, item.getId(),
						itemImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				Bitmap itemPreviewImageBitmap = ImageUtil.refineItemImage(itemImagePath, ImageUtil.ITEM_PREVIEW_IMAGE_WIDTH);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX,
						itemPreviewImageBitmap, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});

				Bitmap itemThumbnailImageBitmap = ImageUtil.refineSquareImage(itemImagePath, ImageUtil.ITEM_THUMBNAIL_IMAGE_SIZE);
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, item.getId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX,
						itemThumbnailImageBitmap, new EntityCallback<String>() {

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
				Toast.makeText(mActivity, getResources().getString(R.string.uploaded), Toast.LENGTH_LONG).show();

				Intent intent = new Intent();
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();
			}
		});
	}


	private void trimContent(){
		mContent.setText(mContent.getText().toString().trim());
		for(BrandInformation brandInformation : mBrandInformationList){
			brandInformation.setBrand(brandInformation.getBrand().trim().replace(" ", "_").replace("\n", ""));
		}
	}


	private boolean isSubmitEnable(){
		return mContent.getText().toString().trim().length() > 0 && mItemImageUri != null;
	}
	
	
	public class BrandInformation {
		private String category;
		private String brand;
		
		public BrandInformation() {
			super();
		}
		public BrandInformation(String category) {
			super();
			this.category = category;
		}
		
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getBrand() {
			return brand;
		}
		public void setBrand(String brand) {
			this.brand = brand;
		}
	}
}
