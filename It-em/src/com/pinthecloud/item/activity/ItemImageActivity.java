package com.pinthecloud.item.activity;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.ImageUtil;

public class ItemImageActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;

	private PhotoViewAttacher mAttacher;
	private ImageView mItemImage;
	private Bitmap mItemImageBitmap;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(0, 0);
		setContentView(R.layout.activity_item_image);

		setToolbar();
		findComponent();
		setComponent();
	}


	@Override
	public void onStart() {
		super.onStart();
		mGaHelper.reportActivityStart(mThisActivity);
		setImageView();
	}


	@Override
	public void onStop() {
		super.onStop();
		mGaHelper.reportActivityStop(mThisActivity);
		mItemImage.setImageBitmap(null);
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}


	@Override
	public View getToolbarLayout() {
		return mToolbarLayout;
	}


	private void setToolbar(){
		mToolbarLayout = findViewById(R.id.toolbar_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);

		mToolbar.setNavigationIcon(R.drawable.appbar_close_ic);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mToolbarLayout.bringToFront();
	}


	private void findComponent(){
		mItemImage = (ImageView)findViewById(R.id.item_image);
	}


	private void setComponent(){
		mAttacher = new PhotoViewAttacher(mItemImage);

		String path = getIntent().getStringExtra(Item.INTENT_KEY);
		mItemImageBitmap = ImageUtil.refineItemImage(path, ImageUtil.ITEM_IMAGE_WIDTH);

		int maxSize = mPrefHelper.getInt(ItConstant.MAX_TEXTURE_SIZE_KEY);
		if(mItemImageBitmap.getHeight() > maxSize){
			int width = mItemImageBitmap.getWidth();
			int height = mItemImageBitmap.getHeight();
			mItemImageBitmap = BitmapUtil.scale(mItemImageBitmap, (int)(width*((float)maxSize/height)), maxSize);
		}
	}


	private void setImageView(){
		mItemImage.setImageBitmap(mItemImageBitmap);
		mAttacher.update();
	}
}
