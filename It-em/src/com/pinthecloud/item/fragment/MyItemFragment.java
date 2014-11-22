package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.etsy.android.grid.StaggeredGridView;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.model.Item;

public class MyItemFragment extends ScrollTabHolderFragment {

	private StaggeredGridView myItemGrid;
	private MyItemGridAdapter myItemGridAdapter;
	private boolean isAdding = false;


	public static ScrollTabHolderFragment newInstance(int position) {
		MyItemFragment fragment = new MyItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(POSITION_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);
		findComponent(view);
		setMyItemGrid(inflater);
		updateMyItemGrid();
		return view;
	}


	@Override
	public void adjustScroll(int scrollHeight) {
		if (scrollHeight != 0 || myItemGrid.getFirstVisiblePosition() < 1) {
		}
	}


	private void findComponent(View view){
		myItemGrid = (StaggeredGridView)view.findViewById(R.id.my_item_frag_grid);
	}


	private void setMyItemGrid(LayoutInflater inflater){
		View header = inflater.inflate(R.layout.row_my_item_grid_header, myItemGrid, false);
		myItemGrid.addHeaderView(header);

		View footer = inflater.inflate(R.layout.row_home_item_list_footer, myItemGrid, false);
		myItemGrid.addFooterView(footer);

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
				if (mScrollTabHolder != null){
					mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, position);
				}

				if (firstVisibleItem + visibleItemCount >= totalItemCount-9 && !isAdding) {
					isAdding = true;
					addNextMyItemGrid();
					isAdding = false;
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
