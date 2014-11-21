package com.pinthecloud.item.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.common.collect.Lists;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.HomeItemListAdapter;
import com.pinthecloud.item.model.Item;

public class HomeFragment extends ItFragment {

	private ProgressBar progressBar;
	private SwipeRefreshLayout homeItemListRefresh;
	private RecyclerView homeItemList;
	private HomeItemListAdapter homeItemListAdapter;
	private RecyclerView.LayoutManager homeItemListLayoutManager;
	private List<Item> itemList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		findComponent(view);
		setRefreshLayout();
		setHomeItemList();
		updateHomeItemList();
		return view;
	}
	
	
	private void findComponent(View view){
		progressBar = (ProgressBar)view.findViewById(R.id.home_frag_progress_bar);
		homeItemListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.home_frag_item_list_refresh);
		homeItemList = (RecyclerView)view.findViewById(R.id.home_frag_item_list);
	}


	private void setRefreshLayout(){
		homeItemListRefresh.setColorSchemeResources(R.color.brand_color, R.color.brand_color_dark, R.color.accent_color);
		homeItemListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateHomeItemList();
				homeItemListRefresh.setRefreshing(false);
			}
		});
	}


	private void setHomeItemList(){
		homeItemList.setHasFixedSize(true);

		homeItemListLayoutManager = new LinearLayoutManager(activity);
		homeItemList.setLayoutManager(homeItemListLayoutManager);

		itemList = Lists.newArrayList();
		homeItemListAdapter = new HomeItemListAdapter(thisFragment, itemList);
		homeItemList.setAdapter(homeItemListAdapter);

		homeItemList.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int lastVisibleItem = ((LinearLayoutManager) homeItemListLayoutManager).findLastVisibleItemPosition();
				int totalItemCount = homeItemListAdapter.getItemCount();

				if (lastVisibleItem >= totalItemCount - 5) {
					addNextHomeItemList();
				}
			}
		});
	}


	private void updateHomeItemList() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			itemList.add(item);
		}
		progressBar.setVisibility(View.GONE);
		homeItemListAdapter.notifyDataSetChanged();
	}


	private void addNextHomeItemList() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			itemList.add(item);
		}
		homeItemListAdapter.notifyDataSetChanged();
	}
}
