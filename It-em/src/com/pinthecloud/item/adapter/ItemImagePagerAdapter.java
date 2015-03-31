package com.pinthecloud.item.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ItLog;

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
		ItLog.log("number : " + mItem.getImageNumber());
		
		return mItem.getImageNumber();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		ItLog.log("isViewFromObject : " + (view == ((ImageView) object)));
		
		return view == ((ImageView) object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ItLog.log("position : " + position);
		
		ImageView image = new ImageView(mActivity);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		image.setLayoutParams(layoutParams);
		image.setAdjustViewBounds(true);
		image.setScaleType(ScaleType.CENTER_INSIDE);
		setImage(image, position);
		((ViewPager) container).addView(image);
		return image;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}

	private void setImage(ImageView image, int position){
		String imageId = position == 0 ? mItem.getId() : mItem.getId() + "_" + position;
		int maxSize = mApp.getPrefHelper().getInt(ItConstant.MAX_TEXTURE_SIZE_KEY);
		if(mItem.getImageHeight() > maxSize){
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(imageId))
			.placeholder(R.drawable.feed_loading_default_img)
			.resize((int)(mItem.getImageWidth()*((float)maxSize/mItem.getImageHeight())), maxSize)
			.into(image);
		} else {
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(imageId))
			.placeholder(R.drawable.feed_loading_default_img)
			.into(image);
		}
	}
}
