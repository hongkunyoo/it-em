package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.BrandListAdapter;
import com.pinthecloud.item.adapter.UploadImageGridAdapter;
import com.pinthecloud.item.dialog.CategoryDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.HashTag;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.SpanUtil;
import com.pinthecloud.item.util.ViewUtil;

public class UploadFragment extends ItFragment {

	public static final String CATEGORY_INTENT_KEY = "CATEGORY_INTENT_KEY";

	private RecyclerView mImageGridView;
	private UploadImageGridAdapter mImageGridAdapter;
	private GridLayoutManager mImageGridLayoutManager;
	private List<String> mImagePathList;

	private EditText mContent;

	private Button mBrandAddButton;
	private RecyclerView mBrandListView;
	private BrandListAdapter mBrandListAdapter;
	private LinearLayoutManager mBrandListLayoutManager;
	private List<Brand> mBrandList;

	private boolean isUploading = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImagePathList = ((UploadActivity)mActivity).getImagePathList();
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
		setImageGrid();
		setBrandList();

		return view;
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
			if(isUploading){
				break;
			}

			isUploading = true;
			mApp.showProgressDialog(mActivity);

			AsyncChainer.asyncChain(mThisFragment, new Chainable(){

				@Override
				public void doNext(final Object obj, Object... params) {
					trimContent();
					String message = checkBrand();
					if(message.equals("")){
						getImageList(obj);
					} else {
						AsyncChainer.notifyNext(obj, message);
					}
				}
			}, new Chainable(){

				@Override
				public void doNext(Object obj, Object... params) {
					String message = params[0].toString();
					if(message.equals("")){
						@SuppressWarnings("unchecked")
						List<Bitmap> imageList = (List<Bitmap>)params[1];
						uploadItem(imageList);
					} else {
						isUploading = false;
						mApp.dismissProgressDialog();
						Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
					}
				}
			});
			break;
		}
		return super.onOptionsItemSelected(menuItem);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.new_item));
	}


	private void findComponent(View view){
		mImageGridView = (RecyclerView)view.findViewById(R.id.upload_frag_image_grid);
		mContent = (EditText)view.findViewById(R.id.upload_frag_content);
		mBrandAddButton = (Button)view.findViewById(R.id.upload_frag_brand_add);
		mBrandListView = (RecyclerView)view.findViewById(R.id.upload_frag_brand_list);
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
		mBrandAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CategoryDialog categoryDialog = new CategoryDialog();
				categoryDialog.setCallback(new DialogCallback() {

					@Override
					public void doPositive(Bundle bundle) {
						categoryDialog.dismiss();

						String category = bundle.getString(CATEGORY_INTENT_KEY);
						mBrandListAdapter.add(mBrandList.size(), new Brand(category));
						mBrandListView.smoothScrollToPosition(mBrandList.size()-1);

						ViewUtil.setListHeightBasedOnChildren(mBrandListView, mBrandListAdapter.getItemCount());
					}
					@Override
					public void doNeutral(Bundle bundle) {
						// Do nothing
					}
					@Override
					public void doNegative(Bundle bundle) {
						// Do nothing
					}
				});
				categoryDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	public void deleteBrand(Brand brand){
		mBrandListAdapter.remove(brand);
		ViewUtil.setListHeightBasedOnChildren(mBrandListView, mBrandListAdapter.getItemCount());
	}


	private void setImageGrid(){
		mImageGridView.setHasFixedSize(true);

		final int gridColumnNum = getResources().getInteger(R.integer.gallery_grid_column_num);
		mImageGridLayoutManager = new GridLayoutManager(mActivity, gridColumnNum);
		mImageGridView.setLayoutManager(mImageGridLayoutManager);
		mImageGridView.setItemAnimator(new DefaultItemAnimator());

		mImageGridAdapter = new UploadImageGridAdapter(mActivity, mThisFragment, mImagePathList);
		mImageGridView.setAdapter(mImageGridAdapter);

		mImageGridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mImageGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mImageGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				int rowCount = (int)Math.ceil((double)mImageGridAdapter.getItemCount()/gridColumnNum);
				ViewUtil.setListHeightBasedOnChildren(mImageGridView, rowCount);
			}
		});
	}


	public void deleteImage(String path){
		mImageGridAdapter.remove(path);
		mActivity.invalidateOptionsMenu();

		int gridColumnNum = mActivity.getResources().getInteger(R.integer.gallery_grid_column_num);
		ViewUtil.setListHeightBasedOnChildren(mImageGridView, (int)Math.ceil((double)mImageGridAdapter.getItemCount()/gridColumnNum));
	}


	private void setBrandList(){
		mBrandListView.setHasFixedSize(true);

		mBrandListLayoutManager = new LinearLayoutManager(mActivity);
		mBrandListView.setLayoutManager(mBrandListLayoutManager);
		mBrandListView.setItemAnimator(new DefaultItemAnimator());

		if(mBrandList == null){
			mBrandList = new ArrayList<Brand>();	
		}
		mBrandListAdapter = new BrandListAdapter(mThisFragment, mBrandList);
		mBrandListView.setAdapter(mBrandListAdapter);

		mBrandListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mBrandListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mBrandListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				ViewUtil.setListHeightBasedOnChildren(mBrandListView, mBrandListAdapter.getItemCount());
			}
		});
	}


	private void getImageList(final Object obj){
		(new AsyncTask<Void,Void,List<Bitmap>>(){

			@Override
			protected List<Bitmap> doInBackground(Void... params) {
				List<Bitmap> imageList = new ArrayList<Bitmap>();
				for(int i=0 ; i<mImagePathList.size() ; i++){
					Bitmap image = ImageUtil.refineItemImage(mImagePathList.get(i), ImageUtil.ITEM_IMAGE_WIDTH, true);
					imageList.add(image);
				}
				return imageList;
			}

			@Override
			protected void onPostExecute(List<Bitmap> imageList) {
				String message = "";
				for(int i=0 ; i<imageList.size() ; i++){
					if(imageList.get(i) == null){
						message = String.format(Locale.US, getResources().getString(R.string.too_big_item_image_message), i+1);
						break;
					}
				}
				AsyncChainer.notifyNext(obj, message, imageList);
			};
		}).execute();
	}


	private void uploadItem(final List<Bitmap> imageList){
		final Item item = getItem(imageList);
		final List<HashTag> tagList = getHashTagList(item.getContent());

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				mAimHelper.addItem(item, tagList, new EntityCallback<Item>() {

					@Override
					public void onCompleted(Item addedItem) {
						item.setId(addedItem.getId());
						item.setRawCreateDateTime(addedItem.getRawCreateDateTime());
						AsyncChainer.notifyNext(obj);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				int jobCount = mImagePathList.size()*2 + 1;
				AsyncChainer.waitChain(jobCount);
				
				for(int i=0 ; i<mImagePathList.size() ; i++){
					uploadItemImage(obj, item, mImagePathList.get(i), imageList.get(i), i);
				}
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				isUploading = false;
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.item_uploaded), Toast.LENGTH_LONG).show();

				Intent intent = new Intent();
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();
			}
		});
	}


	private Item getItem(List<Bitmap> imageList){
		int coverWidth = imageList.get(0).getWidth();
		int coverHeight = imageList.get(0).getHeight();
		
		int mainWidth = 0;
		int mainHeight = 0;
		double maxHeightRatio = 0;
		for(int i=0 ; i<imageList.size() ; i++){
			double heightRatio = (double)imageList.get(i).getHeight()/imageList.get(i).getWidth();
			if(maxHeightRatio < heightRatio){
				maxHeightRatio = heightRatio;
				mainWidth = imageList.get(i).getWidth();
				mainHeight = imageList.get(i).getHeight();
			}
		}

		ItUser myUser = mObjectPrefHelper.get(ItUser.class);
		String content = mContent.getText().toString() + (mBrandList.size()>0 ? "\n\n" : "") + getBrandContent(mBrandList);
		return new Item(content, myUser.getNickName(), myUser.getId(), mImagePathList.size(),
				coverWidth, coverHeight, mainWidth, mainHeight);
	}


	private List<HashTag> getHashTagList(String content){
		List<HashTag> tagList = new ArrayList<HashTag>();
		List<String> hashTags = SpanUtil.getSpanList(content, '#');
		for(String hashTag : hashTags){
			tagList.add(new HashTag(hashTag));
		}
		return tagList;
	}


	private String getBrandContent(List<Brand> originList){
		List<Brand> brandList = new ArrayList<Brand>();
		brandList.addAll(originList);

		Collections.sort(brandList, new Comparator<Brand>(){

			@Override
			public int compare(Brand lhs, Brand rhs) {
				return lhs.getCategory().compareToIgnoreCase(rhs.getCategory());
			}
		});

		String content = "";
		List<String> categoryList = new ArrayList<String>();
		for(Brand brand : brandList){
			if(!categoryList.contains(brand.getCategory())){
				categoryList.add(brand.getCategory());
				content = content + brand.getCategory() + " ";
			}

			int brandIndex = brandList.indexOf(brand);
			content = content + "#" + brand.getBrand() + (brandIndex != brandList.size()-1 ? " " : "");
		}
		return content;
	}


	private void uploadItemImage(final Object obj, final Item item, String imagePath, Bitmap imageBitmap, int index){
		String imageId = index == 0 ? item.getId() : item.getId() + "_" + index;
		uploadImage(obj, imageId, imageBitmap);
		uploadThumbnailImage(obj, imageId, imagePath);
		if(index == 0){
			uploadPreviewImage(obj, imageId, imagePath);
		}
	}


	private void uploadImage(final Object obj, String imageId, Bitmap imageBitmap){
		mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, imageId, imageBitmap,
				new EntityCallback<String>() {

			@Override
			public void onCompleted(String entity) {
				AsyncChainer.notifyNext(obj);
			}
		});
	}
	
	
	private void uploadThumbnailImage(final Object obj, final String imageId, final String imagePath){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				return ImageUtil.refineSquareImage(imagePath, ImageUtil.ITEM_THUMBNAIL_IMAGE_SIZE, true);
			}

			@Override
			protected void onPostExecute(Bitmap thumbnailImage) {
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, imageId+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX,
						thumbnailImage, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});
			};
		}).execute();
	}
	
	
	private void uploadPreviewImage(final Object obj, final String imageId, final String imagePath){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				return ImageUtil.refineItemImage(imagePath, ImageUtil.ITEM_PREVIEW_IMAGE_WIDTH, true);
			}

			@Override
			protected void onPostExecute(Bitmap previewImage) {
				mBlobStorageHelper.uploadBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, imageId+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX,
						previewImage, new EntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						AsyncChainer.notifyNext(obj);
					}
				});
			};
		}).execute();
	}


	private void trimContent(){
		mContent.setText(mContent.getText().toString().trim());
		for(int i=0 ; i<mBrandList.size() ; i++){
			if(mBrandList.get(i).getBrand() == null || mBrandList.get(i).getBrand().equals("")){
				mBrandListAdapter.remove(mBrandList.get(i));
				i--;
			} else {
				mBrandList.get(i).setBrand(
						mBrandList.get(i).getBrand().trim().replace(" ", "_").replace("\n", ""));
			}
		}
	}


	private String checkBrand(){
		String brandRegx = "\\w+";
		for(int i=0 ; i<mBrandList.size() ; i++){
			String brand = mBrandList.get(i).getBrand(); 
			if(!brand.matches(brandRegx)){
				return getResources().getString(R.string.bad_brand_message) + "\n" + brand;
			}
		}
		return "";
	}


	private boolean isSubmitEnable(){
		return mContent.getText().toString().trim().length() > 0 
				&& mImagePathList.size() > 0;
	}


	public class Brand {
		private String category;
		private String brand;

		public Brand() {
			super();
		}
		public Brand(String category) {
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
