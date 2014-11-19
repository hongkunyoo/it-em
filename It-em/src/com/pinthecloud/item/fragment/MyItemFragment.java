package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.model.Item;

public class MyItemFragment extends ItFragment {

	private GridView myItemGrid;
	private MyItemGridAdapter myItemGridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);
		findComponent(view);
		setMyItemGrid();
		updateMyItemGrid();
		return view;
	}


	private void findComponent(View view){
		myItemGrid = (GridView)view.findViewById(R.id.my_item_frag_grid);
	}


	private void setMyItemGrid(){
		myItemGridAdapter = new MyItemGridAdapter(activity, thisFragment);
		myItemGrid.setAdapter(myItemGridAdapter);

		myItemGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		myItemGrid.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount-9) {
					addNextMyItemGrid();
				}
			}
		});
	}


	private void updateMyItemGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			myItemGridAdapter.add(item);
		}
	}


	private void addNextMyItemGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			myItemGridAdapter.add(item);
		}
	}
}
