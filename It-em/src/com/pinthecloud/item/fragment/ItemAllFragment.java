package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ItemAllGridAdapter;
import com.pinthecloud.item.model.Item;

public class ItemAllFragment extends ItFragment {

	private SwipeRefreshLayout itemAllGridRefresh;
	private GridView itemAllGrid;
	private ItemAllGridAdapter itemAllGridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item_all, container, false);
		findComponent(view);
		setItemAllGrid();
		updateItemAllGrid();
		return view;
	}


	private void findComponent(View view){
		itemAllGridRefresh = (SwipeRefreshLayout)view.findViewById(R.id.item_all_frag_grid_refresh);
		itemAllGrid = (GridView)view.findViewById(R.id.item_all_frag_grid);
	}


	private void setItemAllGrid(){
		itemAllGridAdapter = new ItemAllGridAdapter(activity, thisFragment);
		itemAllGrid.setAdapter(itemAllGridAdapter);

		itemAllGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		itemAllGridRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateItemAllGrid();
				itemAllGridRefresh.setRefreshing(false);
			}
		});
	}


	private void updateItemAllGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			itemAllGridAdapter.add(item);
		}
	}
}
