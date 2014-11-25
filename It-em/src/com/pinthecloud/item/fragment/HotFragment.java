package com.pinthecloud.item.fragment;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.HotItemListAdapter;

public class HotFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mListRefresh;
	private StickyListHeadersListView mListView;
	private HotItemListAdapter mListAdapter;
	private boolean mIsAdding = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_hot, container, false);
		findComponent(view);
		setRefreshLayout();
		setList();
		updateList();
		return view;
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.hot_frag_progress_bar);
		mListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.hot_frag_item_list_refresh);
		mListView = (StickyListHeadersListView)view.findViewById(R.id.hot_frag_item_list);
	}


	private void setRefreshLayout(){
		mListRefresh.setColorSchemeResources(R.color.brand_color, R.color.brand_color_dark, R.color.accent_color);
		mListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateList();
				mListRefresh.setRefreshing(false);
			}
		});
	}


	@SuppressLint("InflateParams")
	private void setList(){
		View footer = mActivity.getLayoutInflater().inflate(R.layout.row_home_item_list_footer, null);
		mListView.addFooterView(footer);

		mListAdapter = new HotItemListAdapter(mActivity, mThisFragment);
		mListView.setAdapter(mListAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount-3 && !mIsAdding) {
					mIsAdding = true;
					addNextItemList();
					mIsAdding = false;
				}
			}
		});
	}


	private void updateList() {
		mProgressBar.setVisibility(View.GONE);
	}


	private void addNextItemList() {
	}
}
