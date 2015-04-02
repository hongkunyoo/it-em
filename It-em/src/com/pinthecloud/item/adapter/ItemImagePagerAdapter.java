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
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.squareup.picasso.Callback;

public class ItemImagePagerAdapter extends PagerAdapter {

	private final double MAX_HEIGHT_RATIO = 1.6;

	private ItApplication mApp;
	private ItActivity mActivity;
	private Item mItem;

	public ItemImagePagerAdapter(ItActivity activity, Item item) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mItem = item;
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
		DynamicHeightImageView image = new DynamicHeightImageView(mActivity);
		image.setAdjustViewBounds(true);
		image.setScaleType(ScaleType.CENTER_CROP);
		
		String imageId = position == 0 ? mItem.getId() : mItem.getId() + "_" + position;
		setButton(image, imageId);
		setImageView(image, imageId);
		
		((ViewPager)container).addView(image);
		return image;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {    
		((ViewPager)container).removeView((DynamicHeightImageView)object);
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
				mActivity.startActivity(intent);
			}
		});
	}

	private void setImageView(final DynamicHeightImageView image, String imageId){
		double heightRatio = Math.min((double)mItem.getImageHeight()/mItem.getImageWidth(), MAX_HEIGHT_RATIO);
		image.setHeightRatio(heightRatio);
		
		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(imageId))
		.placeholder(R.drawable.feed_loading_default_img)
		.into(image, new Callback(){

			@Override
			public void onError() {
			}
			@Override
			public void onSuccess() {
				image.setScaleType(ScaleType.FIT_CENTER);
			}
		});
	}
}
