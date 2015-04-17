package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ItemImagePagerAdapter;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.pinthecloud.item.view.HeightBasedOnChildrenViewPager;

public class ItemImageActivity extends ItActivity {

	public static final String POSITION_KEY = "POSITION_KEY";

	private View mToolbarLayout;
	private Toolbar mToolbar;

	private HeightBasedOnChildrenViewPager mImagePager;
	private ItemImagePagerAdapter mImagePagerAdapter;
	private TextView mImageNumber;

	private Item mItem; 
	private int mPosition;
	private boolean[] mUpdatedList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_pop_up, 0);
		setContentView(R.layout.activity_item_image);

		mItem = getIntent().getParcelableExtra(Item.INTENT_KEY);
		mPosition = getIntent().getIntExtra(POSITION_KEY, 0);
		setToolbar();
		findComponent();
		setComponent();
	}


	@Override
	public void onStart() {
		super.onStart();
		mGaHelper.reportActivityStart(mThisActivity);
	}


	@Override
	public void onStop() {
		super.onStop();
		mGaHelper.reportActivityStop(mThisActivity);
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


	private void findComponent(){
		mImagePager = (HeightBasedOnChildrenViewPager)findViewById(R.id.item_image_pager);
		mImageNumber = (TextView)findViewById(R.id.item_image_number);
	}


	private void setComponent(){
		mImagePagerAdapter = new ItemImagePagerAdapter(mThisActivity, mItem);
		mImagePager.setOffscreenPageLimit(mImagePagerAdapter.getCount());
		mImagePager.setAdapter(mImagePagerAdapter);
		mImagePager.setCurrentItem(mPosition);

		mImageNumber.setText((mPosition+1) + "/" + mItem.getImageNumber());
		
		mUpdatedList = new boolean[mImagePagerAdapter.getCount()];
		mImagePager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(!mUpdatedList[position]){
					mUpdatedList[position] = true;
					ImageView imageView = (DynamicHeightImageView)mImagePager.findViewWithTag(position);
					mImagePagerAdapter.setImageView(imageView, position);
				}
			}
			@Override
			public void onPageSelected(int position) {
				mImageNumber.setText((position+1) + "/" + mImagePagerAdapter.getCount());
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
}
