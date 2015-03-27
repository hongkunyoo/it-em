package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItemImageActivity;
import com.pinthecloud.item.adapter.BrandInfoListAdapter;
import com.pinthecloud.item.dialog.CategoryDialog;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.HashTag;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.TextUtil;

public class UploadFragment extends ItFragment {

	public static final String CATEGORY_INTENT_KEY = "CATEGORY_INTENT_KEY";

	private String[] mPaths;
	private Uri mItemImageUri;

	private ImageView mItemImage;
	private ImageButton mItemImageDelete;
	private EditText mContent;

	private Button mBrandInfoAddButton;
	private RecyclerView mListView;
	private BrandInfoListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<BrandInfo> mBrandInfoList;

	private ItUser mMyItUser;


	public static ItFragment newInstance(String[] paths) {
		ItFragment fragment = new UploadFragment();
		Bundle bundle = new Bundle();
		bundle.putStringArray(GalleryFragment.GALLERY_PATHS_KEY, paths);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
		mPaths = getArguments().getStringArray(GalleryFragment.GALLERY_PATHS_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_upload, container, false);
		
		mGaHelper.sendScreen(mThisFragment);
		setHasOptionsMenu(true);
		setActionBar();
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

		mItemImageDelete.setVisibility(mItemImageUri != null ? View.VISIBLE : View.GONE);
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
		MenuItem menuItem = menu.findItem(R.id.upload_menu_submit);
		menuItem.setEnabled(isSubmitEnable());
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.upload_menu_submit:
			trimContent();
			String message = checkBrand();
			if(message.equals("")){
				uploadItem();
			} else {
				Toast toast = Toast.makeText(mActivity, message, Toast.LENGTH_LONG);
				TextView textView = (TextView)toast.getView().findViewById(android.R.id.message);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				toast.show();
			}
			break;
		}
		return super.onOptionsItemSelected(menuItem);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.new_item));
	}
	
	
	private void findComponent(View view){
		mItemImage = (ImageView)view.findViewById(R.id.upload_frag_item_image);
		mItemImageDelete = (ImageButton)view.findViewById(R.id.upload_frag_item_image_delete);
		mContent = (EditText)view.findViewById(R.id.upload_frag_content);
		mBrandInfoAddButton = (Button)view.findViewById(R.id.upload_frag_brand_info_add);
		mListView = (RecyclerView)view.findViewById(R.id.upload_frag_brand_info_list);
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
				if(mItemImageUri == null){
					String[] itemList = getResources().getStringArray(R.array.upload_image_select_array);
					DialogCallback[] callbacks = getDialogCallbacks(itemList);

					ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
					listDialog.setCallbacks(callbacks);
					listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
				} else {
					Intent intent = new Intent(mActivity, ItemImageActivity.class);
					intent.putExtra(Item.INTENT_KEY, mItemImageUri);
					startActivity(intent);
				}
			}
		});

		mItemImageDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Set profile image default
				mItemImageUri = null;
				mItemImage.setImageResource(R.drawable.upload_thumbnail_default_img);

				mActivity.invalidateOptionsMenu();
				mItemImageDelete.setVisibility(View.GONE);
			}
		});

		mBrandInfoAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CategoryDialog categoryDialog = new CategoryDialog();
				categoryDialog.setCallback(new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						String category = bundle.getString(CATEGORY_INTENT_KEY);
						mListAdapter.add(mBrandInfoList.size(), new BrandInfo(category));
						mListView.smoothScrollToPosition(mBrandInfoList.size()-1);
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

		mBrandInfoList = new ArrayList<BrandInfo>();
		mListAdapter = new BrandInfoListAdapter(mBrandInfoList);
		mListView.setAdapter(mListAdapter);
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
			}
		}

		return callbacks;
	}


	private void uploadItem(){
		mApp.showProgressDialog(mActivity);

		final String itemImagePath = FileUtil.getMediaPathFromGalleryUri(mActivity, mItemImageUri);
		final Bitmap itemImageBitmap = ImageUtil.refineItemImage(itemImagePath, ImageUtil.ITEM_IMAGE_WIDTH);

		String content = mContent.getText().toString() + (mBrandInfoList.size()>0 ? "\n\n" : "")
				+ getBrandInfoContent(mBrandInfoList);
		final Item item = new Item(content, mMyItUser.getNickName(), mMyItUser.getId(),
				itemImageBitmap.getWidth(), itemImageBitmap.getHeight());

		final List<HashTag> tagList = new ArrayList<HashTag>();
		List<String> hashTags = TextUtil.getSpanBodyList(content);
		for(String hashTag : hashTags){
			tagList.add(new HashTag(hashTag));
		}

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				mAimHelper.addItem(item, tagList, new EntityCallback<Item>() {

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
				uploadItemImage(obj, item, itemImagePath, itemImageBitmap);
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


	private String getBrandInfoContent(List<BrandInfo> originList){
		List<BrandInfo> brandInfoList = new ArrayList<BrandInfo>();
		brandInfoList.addAll(originList);

		Collections.sort(brandInfoList, new Comparator<BrandInfo>(){

			@Override
			public int compare(BrandInfo lhs, BrandInfo rhs) {
				return lhs.getCategory().compareToIgnoreCase(rhs.getCategory());
			}
		});

		String content = "";
		List<String> categoryList = new ArrayList<String>();
		for(BrandInfo brandInfo : brandInfoList){
			if(!categoryList.contains(brandInfo.getCategory())){
				categoryList.add(brandInfo.getCategory());
				content = content + brandInfo.getCategory() + " ";
			}
			content = content + "#" + brandInfo.getBrand() + 
					(brandInfoList.indexOf(brandInfo) != brandInfoList.size()-1 ? " " : "");
		}
		return content;
	}


	private void uploadItemImage(final Object obj, Item item, String itemImagePath, Bitmap itemImageBitmap){
		AsyncChainer.waitChain(2);

		mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, item.getId(),
				itemImageBitmap, new EntityCallback<String>() {

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


	private void trimContent(){
		mContent.setText(mContent.getText().toString().trim());
		for(int i=0 ; i<mBrandInfoList.size() ; i++){
			if(mBrandInfoList.get(i).getBrand() == null || mBrandInfoList.get(i).getBrand().equals("")){
				mListAdapter.remove(mBrandInfoList.get(i));
				i--;
			} else {
				mBrandInfoList.get(i).setBrand(
						mBrandInfoList.get(i).getBrand().trim().replace(" ", "_").replace("\n", ""));
			}
		}
	}


	private String checkBrand(){
		String brandRegx = "\\w+";
		for(int i=0 ; i<mBrandInfoList.size() ; i++){
			String brand = mBrandInfoList.get(i).getBrand(); 
			if(!brand.matches(brandRegx)){
				return getResources().getString(R.string.bad_brand_message) + "\n" + brand;
			}
		}
		return "";
	}


	private boolean isSubmitEnable(){
		return mContent.getText().toString().trim().length() > 0 && mItemImageUri != null;
	}


	public class BrandInfo {
		private String category;
		private String brand;

		public BrandInfo() {
			super();
		}
		public BrandInfo(String category) {
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
