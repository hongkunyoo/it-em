package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MainPagerAdapter;
import com.pinthecloud.item.dialog.GuideDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.NotificationEvent;
import com.pinthecloud.item.interfaces.MainTabHolder;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MainActivity extends ItActivity {

	private final int MAX_NOTI_NUMBER = 99;

	private PagerSlidingTabStrip mTab;
	private ViewPager mViewPager;
	private MainPagerAdapter mViewPagerAdapter;
	private boolean[] mTabUpdatedList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findComponent();
		setViewPager();
		setTab();
		setTabImage();
		setNotiTab();

		if(!mPrefHelper.getBoolean(ItConstant.GUIDE_READ_KEY)){
			showGuide();
		}
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
		mViewPagerAdapter.setTabHolder(new MainTabHolder() {

			@Override
			public void updateNotiTab() {
				setNotiTab();
			}
			@Override
			public void updateFragment() {
			}
		});

		mViewPager.setOffscreenPageLimit(mViewPagerAdapter.getCount());
		mViewPager.setAdapter(mViewPagerAdapter);
	}


	private void setTab(){
		mTabUpdatedList = new boolean[mViewPagerAdapter.getCount()];
		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				SparseArrayCompat<MainTabHolder> tabHolderList = mViewPagerAdapter.getTabHolderList();
				MainTabHolder fragmentContent = tabHolderList.valueAt(position);
				if(!mTabUpdatedList[position]){
					mTabUpdatedList[position] = true;
					fragmentContent.updateFragment();
				}
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


	private void showGuide(){
		GuideDialog guideDialog = new GuideDialog();
		guideDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
	}


	public void onEvent(NotificationEvent event){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mThisActivity, getResources().getString(R.string.new_noti), Toast.LENGTH_LONG).show();
			}
		});
		setNotiTab();
	}
}
