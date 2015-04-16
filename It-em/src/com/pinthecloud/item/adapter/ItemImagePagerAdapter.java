package com.pinthecloud.item.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.DynamicHeightImageView;

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
		DynamicHeightImageView imageView = new DynamicHeightImageView(mActivity);
		imageView.setAdjustViewBounds(true);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setHeightRatio((double)mItem.getMainImageHeight()/mItem.getMainImageWidth());
		imageView.setImageResource(R.drawable.feed_loading_default_img);

		String imageId = position == 0 ? mItem.getId() : mItem.getId() + "_" + position;
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


	private void setImageView(final DynamicHeightImageView imageView, String imageId){
		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(imageId))
		.placeholder(R.drawable.feed_loading_default_img)
		.into(imageView);
	}
}
