package com.pinthecloud.item.activity;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.ViewUtil;
import com.pinthecloud.item.view.DynamicHeightImageView;

public class UploadImageActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;

	private PhotoViewAttacher mAttacher;
	private DynamicHeightImageView mImageView;
	private Bitmap mImage;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_pop_up, 0);
		setContentView(R.layout.activity_upload_image);

		setToolbar();
		setComponent();
		getImage();
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
		ViewUtil.recycleImageView(mImageView);
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.pop_in, R.anim.slide_out_pop_down);
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


	private void setComponent(){
		mImageView = (DynamicHeightImageView)findViewById(R.id.upload_image);
		mAttacher = new PhotoViewAttacher(mImageView);
	}


	private void getImage(){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				String path = getIntent().getStringExtra(Item.INTENT_KEY);
				return ImageUtil.refineItemImage(path, ImageUtil.ITEM_IMAGE_WIDTH, true);
			}

			@Override
			protected void onPostExecute(Bitmap image) {
				mImage = image;
				setImageView();
			};
		}).execute();
	}


	private void setImageView(){
		mImageView.setImageBitmap(mImage);
		mAttacher.update();
	}
}
