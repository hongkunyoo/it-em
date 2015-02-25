package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MainPagerAdapter;
import com.pinthecloud.item.event.NotificationEvent;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MainActivity extends ItActivity {

	private final int MAX_NOTI_NUMBER = 99;

	private PagerSlidingTabStrip mTab;
	private ViewPager mViewPager;
	private MainPagerAdapter mViewPagerAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findComponent();
		setViewPager();
		setTab();
		setTabImage();
		setNotiTab();
	}


	@Override
	protected void onStart() {
		super.onStart();

	}


	@Override
	public View getToolbarLayout() {
		return null;
	}


	private void findComponent(){
		mTab = (PagerSlidingTabStrip)findViewById(R.id.main_tab);
		mViewPager = (ViewPager)findViewById(R.id.main_pager); 
	}


	private void setViewPager(){
		mViewPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mThisActivity);
		mViewPager.setOffscreenPageLimit(mViewPagerAdapter.getCount());
		mViewPager.setAdapter(mViewPagerAdapter);
	}


	private void setTab(){
		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageSelected(int position) {
				if(position == MainPagerAdapter.TAB.NOTI.ordinal()){
					mPrefHelper.remove(ItConstant.NOTIFICATION_NUMBER_KEY);
					setNotiTab();
				}
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	private void setTabImage(){
		for(int i=0 ; i<mViewPagerAdapter.getCount() ; i++){
			View tab = mTab.getTab(i);
			ImageView image = (ImageView)tab.findViewById(R.id.tab_main_image);
			image.setImageResource(mViewPagerAdapter.getPageIconResId(i));
		}
	}


	private void setNotiTab(){
		View tab = mTab.getTab(MainPagerAdapter.TAB.NOTI.ordinal());
		final TextView number = (TextView)tab.findViewById(R.id.tab_main_number);
		final int notiNumber = mPrefHelper.getInt(ItConstant.NOTIFICATION_NUMBER_KEY);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				number.setText(notiNumber > MAX_NOTI_NUMBER ? "+"+MAX_NOTI_NUMBER : ""+notiNumber);
				number.setVisibility(notiNumber > 0 ? View.VISIBLE : View.GONE);
			}
		});
	}


	public void onEvent(NotificationEvent event){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mThisActivity, getResources().getString(R.string.noti_new), Toast.LENGTH_LONG).show();
			}
		});
		setNotiTab();
	}
}
