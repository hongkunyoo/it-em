package com.pinthecloud.item.adapter;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ImageActivity;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ViewUtil;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.squareup.picasso.Callback;

public class ItemImagePagerAdapter extends PagerAdapter {

	private ItApplication mApp;
	private ItActivity mActivity;
	private Item mItem;
	private double MAX_HEIGHT_RATIO;

	public ItemImagePagerAdapter(ItActivity activity, Item item) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mItem = item;
		
		int width = ViewUtil.getDeviceWidth(activity);
		int height = ViewUtil.getDeviceHeight(activity);
		int hiddenHeight = (int)(ViewUtil.getActionBarHeight(activity)*2.5 + ViewUtil.getStatusBarHeight(activity));
		this.MAX_HEIGHT_RATIO = (double)(height-hiddenHeight)/width;
	}

	@Override
	public int getCount() {
		return mItem.getImageNumber();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (DynamicHeightImageView)object; 
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		double heightRatio = (double)mItem.getMainImageHeight()/mItem.getMainImageWidth();
		DynamicHeightImageView imageView = new DynamicHeightImageView(mActivity);
		imageView.setAdjustViewBounds(true);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setHeightRatio(Math.min(heightRatio, MAX_HEIGHT_RATIO));
		imageView.setImageResource(R.drawable.feed_loading_default_img);
		
		String imageId = position == 0 ? mItem.getId() : mItem.getId() + "_" + position;
		setButton(imageView, imageId);
		setImageView(imageView, imageId);
		
		((ViewPager)container).addView(imageView);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		DynamicHeightImageView imageView = (DynamicHeightImageView)object;
		imageView.setImageBitmap(null);
		((ViewPager)container).removeView(imageView);
	}

	@Override 
	public Parcelable saveState() { 
		return null; 
	}

	private void setButton(DynamicHeightImageView image, final String imageId){
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ImageActivity.class);
				intent.putExtra(Item.INTENT_KEY, BlobStorageHelper.getItemImgUrl(imageId));
				intent.putExtra(ImageActivity.FROM_INTERNET_KEY, true);
				mActivity.startActivity(intent);
			}
		});
	}

	private void setImageView(final DynamicHeightImageView imageView, String imageId){
		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(imageId))
		.placeholder(R.drawable.feed_loading_default_img)
		.into(imageView, new Callback(){

			@Override
			public void onError() {
			}
			@Override
			public void onSuccess() {
				imageView.setScaleType(ScaleType.FIT_CENTER);
			}
		});
	}
}
