package com.pinthecloud.item.fragment;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ItemRankListAdapter;
import com.pinthecloud.item.model.Item;

public class ItemRankFragment extends ItFragment {

	private SwipeRefreshLayout itemRankListRefresh;
	private StickyListHeadersListView itemRankList;
	private ItemRankListAdapter itemRankListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item_rank, container, false);
		findComponent(view);
		setItemRankList();
		updateItemRankList();
		return view;
	}


	private void findComponent(View view){
		itemRankListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.item_rank_frag_list_refresh);
		itemRankList = (StickyListHeadersListView)view.findViewById(R.id.item_rank_frag_list);
	}


	private void setItemRankList(){
		itemRankListAdapter = new ItemRankListAdapter(activity, thisFragment);
		itemRankList.setAdapter(itemRankListAdapter);

		itemRankList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		itemRankListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateItemRankList();
				itemRankListRefresh.setRefreshing(false);
			}
		});
	}


	private void updateItemRankList() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			itemRankListAdapter.add(item);
		}
	}
}
