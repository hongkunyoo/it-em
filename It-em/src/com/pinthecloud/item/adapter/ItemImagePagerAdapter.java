package com.pinthecloud.item.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.squareup.picasso.Callback;

public class ItemImagePagerAdapter extends PagerAdapter {

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
		setImageView(image, position);
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

	private void setImageView(final DynamicHeightImageView image, int position){
		image.setHeightRatio((double)mItem.getImageHeight()/mItem.getImageWidth());

		String imageId = position == 0 ? mItem.getId() : mItem.getId() + "_" + position;
		int maxSize = mApp.getPrefHelper().getInt(ItConstant.MAX_TEXTURE_SIZE_KEY);
		if(mItem.getImageHeight() > maxSize){
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(imageId))
			.placeholder(R.drawable.feed_loading_default_img)
			.resize((int)(mItem.getImageWidth()*((float)maxSize/mItem.getImageHeight())), maxSize)
			.into(image, new Callback(){

				@Override
				public void onError() {
				}
				@Override
				public void onSuccess() {
					image.setScaleType(ScaleType.FIT_CENTER);
				}
			});
		} else {
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
}
