package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyPagePagerAdapter;
import com.pinthecloud.item.interfaces.ScrollTabHolder;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MyPageFragment extends ItFragment implements ScrollTabHolder {

	private FrameLayout mHeader;
	public static int mHeaderHeight;
	public static int mTabHeight;

	private TextView nickNameText;

	private PagerSlidingTabStrip tab;
	private ViewPager mViewPager;
	private MyPagePagerAdapter myPagePagerAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_page, container, false);
		findComponent(view);
		setComponent();
		setTab();
		return view;
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
		if (mViewPager.getCurrentItem() == pagePosition) {
			int scrollY = getScrollY(view, firstVisibleItem);
			ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, -(mHeaderHeight - mTabHeight)));
		}
	}

	@Override
	public void adjustScroll(int scrollHeight) {
	}


	public int getScrollY(AbsListView view, int firstVisiblePosition) {
		View c = view.getChildAt(0);
		if(c == null){
			return 0;
		}
		int headerHeight = 0;
		if (firstVisiblePosition >= 1) {
			headerHeight = mHeaderHeight;
		}
		return -c.getTop() + firstVisiblePosition * c.getHeight() + headerHeight;
	}


	private void findComponent(View view){
		mHeader = (FrameLayout)view.findViewById(R.id.my_page_frag_header_layout);
		nickNameText = (TextView)view.findViewById(R.id.my_page_frag_nick_name);
		tab = (PagerSlidingTabStrip) view.findViewById(R.id.my_page_frag_tab);
		mViewPager = (ViewPager)view.findViewById(R.id.my_page_frag_pager);
	}


	private void setComponent(){
		mTabHeight = getResources().getDimensionPixelSize(R.dimen.main_tab_height);
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.my_page_header_height);
	}


	private void setTab(){
		myPagePagerAdapter = new MyPagePagerAdapter(getFragmentManager(), activity);
		myPagePagerAdapter.setTabHolderScrollingContent(this);
		mViewPager.setAdapter(myPagePagerAdapter);
		tab.setViewPager(mViewPager);
		tab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				SparseArrayCompat<ScrollTabHolder> scrollTabHolders = myPagePagerAdapter.getScrollTabHolders();
				ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
				currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader)));
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
}
