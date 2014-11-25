package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyPagePagerAdapter;
import com.pinthecloud.item.interfaces.ScrollTabHolder;
import com.pinthecloud.item.view.NoPageTransformer;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class MyPageFragment extends ItFragment {

	public static int mTabHeight;

	private LinearLayout mHeader;
	private TextView mNickNameText;

	private PagerSlidingTabStrip mTab;
	private ViewPager mViewPager;
	private MyPagePagerAdapter mViewPagerAdapter;


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


	private void findComponent(View view){
		mHeader = (LinearLayout)view.findViewById(R.id.my_page_frag_header_layout);
		mNickNameText = (TextView)view.findViewById(R.id.my_page_frag_nick_name);
		mTab = (PagerSlidingTabStrip) view.findViewById(R.id.my_page_frag_tab);
		mViewPager = (ViewPager)view.findViewById(R.id.my_page_frag_pager);
	}


	private void setComponent(){
		mTabHeight = mTab.getHeight();
	}


	private void setTab(){
		mViewPagerAdapter = new MyPagePagerAdapter(getFragmentManager(), mActivity);
		mViewPagerAdapter.setTabHolderScrollingContent(new ScrollTabHolder() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
				if (mViewPager.getCurrentItem() == pagePosition) {
					int scrollY = getListScrollY(view);
					mHeader.scrollTo(0, Math.min(scrollY, mHeader.getHeight() - mTab.getHeight()));
				}
			}
			@Override
			public void adjustScroll(int scrollHeight) {
			}
		});

		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setPageTransformer(false, new NoPageTransformer());

		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mViewPagerAdapter.getScrollTabHolders();
				ScrollTabHolder fragmentContent = scrollTabHolders.valueAt(position);
				fragmentContent.adjustScroll((int) (mHeader.getHeight() - mHeader.getScrollY()));
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}


	private int getListScrollY(AbsListView view) {
		View c = view.getChildAt(0);
		if(c == null){
			return 0;
		}
		int headerHeight = 0;
		if (view.getFirstVisiblePosition() >= 1) {
			headerHeight = mHeader.getHeight();
		}
		return -c.getTop() + view.getFirstVisiblePosition() * c.getHeight() + headerHeight;
	}
}
