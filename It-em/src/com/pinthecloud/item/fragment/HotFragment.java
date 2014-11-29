package com.pinthecloud.item.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.google.common.collect.Lists;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.HotItemListAdapter;
import com.pinthecloud.item.adapter.HotItemListHeaderAdapter;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.ItDateTime;
import com.pinthecloud.item.model.Item;

public class HotFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mListRefresh;
	private RecyclerView mListView;
	private HotItemListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private boolean mIsAdding = false;
	private ItDateTime currentDate = ItDateTime.getToday();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_hot, container, false);
		findComponent(view);
		setRefreshLayout();
		setList();
		updateList(currentDate, null);
		return view;
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.hot_frag_progress_bar);
		mListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.hot_frag_item_list_refresh);
		mListView = (RecyclerView)view.findViewById(R.id.hot_frag_item_list);
	}


	private void setRefreshLayout(){
		mListRefresh.setColorSchemeResources(R.color.brand_color, R.color.brand_color_dark, R.color.accent_color);
		mListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateList(currentDate, null);
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mItemList = Lists.newArrayList();
		mListAdapter = new HotItemListAdapter(mActivity, mThisFragment, mItemList);
		mListView.setAdapter(mListAdapter);

		StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
		.setAdapter(mListAdapter)
		.setRecyclerView(mListView)
		.setStickyHeadersAdapter(new HotItemListHeaderAdapter(mItemList), true)
		.build();
		mListView.addItemDecoration(decoration);

		mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int lastVisibleItem = mListLayoutManager.findLastVisibleItemPosition();
				int totalItemCount = mListLayoutManager.getItemCount();

				if (lastVisibleItem >= totalItemCount-3 && !mIsAdding) {
					addNextItemList();
				}
			}
		});
	}


	private void updateList(ItDateTime dateTime, final ItEntityCallback<Boolean> callback) {
		mAimHelper.getRank10(mThisFragment, dateTime, new ItListCallback<Item>() {
	
			@Override
			public void onCompleted(List<Item> list, int count) {
				for (int i = 0 ; i < count ; i++) {
					mListAdapter.add(list.get(i), i);
				}
				mProgressBar.setVisibility(View.GONE);
				mListRefresh.setRefreshing(false);
				mListAdapter.notifyDataSetChanged();
	
				if (callback != null){
					callback.onCompleted(true);	
				}
			}
		});
	}


	private void addNextItemList() {
		mIsAdding = true;
		mListAdapter.setHasFooter(true);
		mListAdapter.notifyDataSetChanged();

		currentDate = currentDate.getYesterday();
		updateList(currentDate, new ItEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				mIsAdding = false;
				mListAdapter.setHasFooter(false);
				mListAdapter.notifyDataSetChanged();
			}
		});
	}
}
