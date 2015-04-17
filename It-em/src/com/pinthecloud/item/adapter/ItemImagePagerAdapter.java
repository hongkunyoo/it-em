package com.pinthecloud.item.adapter;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.pinthecloud.item.ItApplication;
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
		DynamicHeightImageView imageView = new DynamicHeightImageView(mActivity);
		imageView.setAdjustViewBounds(true);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setHeightRatio((double)mItem.getMainImageHeight()/mItem.getMainImageWidth());
		imageView.setTag(position);

		((ViewPager)container).addView(imageView);
		return imageView;
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		ImageView imageView = (DynamicHeightImageView)object;
		imageView.setImageBitmap(null);
		((ViewPager)container).removeView(imageView);
	}


	@Override 
	public Parcelable saveState() { 
		return null; 
	}


	public void setImageView(final ImageView imageView, int position){
		String imageId = position == 0 ? mItem.getId() : mItem.getId() + "_" + position;
		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(imageId))
		.into(imageView, new Callback(){

			@Override
			public void onError() {
			}
			@Override
			public void onSuccess() {
				PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
				attacher.update();
			}
		});
	}
}
