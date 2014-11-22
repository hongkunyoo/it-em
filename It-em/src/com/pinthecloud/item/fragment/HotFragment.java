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
import com.pinthecloud.item.model.Item;

public class HotFragment extends ItFragment {

	private ProgressBar progressBar;
	private SwipeRefreshLayout hotItemListRefresh;
	private StickyListHeadersListView hotItemList;
	private HotItemListAdapter hotItemListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_hot, container, false);
		findComponent(view);
		setRefreshLayout();
		setHotItemList();
		updateHotItemList();
		return view;
	}


	private void findComponent(View view){
		progressBar = (ProgressBar)view.findViewById(R.id.hot_frag_progress_bar);
		hotItemListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.hot_frag_item_list_refresh);
		hotItemList = (StickyListHeadersListView)view.findViewById(R.id.hot_frag_item_list);
	}


	private void setRefreshLayout(){
		hotItemListRefresh.setColorSchemeResources(R.color.brand_color, R.color.brand_color_dark, R.color.accent_color);
		hotItemListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateHotItemList();
				hotItemListRefresh.setRefreshing(false);
			}
		});
	}


	@SuppressLint("InflateParams")
	private void setHotItemList(){
		View footer = activity.getLayoutInflater().inflate(R.layout.row_home_item_list_footer, null);
		hotItemList.addFooterView(footer);

		hotItemListAdapter = new HotItemListAdapter(activity, thisFragment);
		hotItemList.setAdapter(hotItemListAdapter);

		hotItemList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		hotItemList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount-5) {
					addNextHotItemList();
				}
			}
		});
	}


	private void updateHotItemList() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			hotItemListAdapter.add(item);
		}
		progressBar.setVisibility(View.GONE);
	}


	private void addNextHotItemList() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			hotItemListAdapter.add(item);
		}
	}
}
