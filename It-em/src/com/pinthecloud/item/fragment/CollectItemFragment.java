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
import com.pinthecloud.item.adapter.CollectItemGridAdapter;
import com.pinthecloud.item.model.Item;

public class CollectItemFragment extends ScrollTabHolderFragment {

	private StaggeredGridView collectItemGrid;
	private CollectItemGridAdapter collectItemGridAdapter;
	private boolean isAdding = false;


	public static ScrollTabHolderFragment newInstance(int position) {
		CollectItemFragment fragment = new CollectItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(POSITION_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_collect_item, container, false);
		findComponent(view);
		setCollectItemGrid(inflater);
		updateCollectItemGrid();
		return view;
	}


	@Override
	public void adjustScroll(final int scrollHeight) {
		if (scrollHeight - MyPageFragment.mTabHeight == 0 
				&& collectItemGrid.getScrollY() > MyPageFragment.mHeaderHeight - MyPageFragment.mTabHeight) {
			return;
		}
		//		collectItemGrid.scrollTo(collectItemGrid.getScrollX(), -scrollHeight + mHeaderHeight);
		collectItemGrid.post(new Runnable() {

			@Override
			public void run() {
				collectItemGrid.smoothScrollBy(-scrollHeight + MyPageFragment.mHeaderHeight, 100);
				collectItemGrid.smoothScrollToPositionFromTop(1, scrollHeight, 20);
			}
		});
	}


	private void findComponent(View view){
		collectItemGrid = (StaggeredGridView)view.findViewById(R.id.collect_item_frag_grid);
	}


	private void setCollectItemGrid(LayoutInflater inflater){
		View header = inflater.inflate(R.layout.row_my_item_grid_header, collectItemGrid, false);
		collectItemGrid.addHeaderView(header);

		View footer = inflater.inflate(R.layout.row_home_item_list_footer, collectItemGrid, false);
		collectItemGrid.addFooterView(footer);

		collectItemGridAdapter = new CollectItemGridAdapter(activity, thisFragment);
		collectItemGrid.setAdapter(collectItemGridAdapter);

		collectItemGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		collectItemGrid.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mScrollTabHolder != null){
					mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
				}

				if (firstVisibleItem + visibleItemCount >= totalItemCount-9 && !isAdding) {
					isAdding = true;
					addNextCollectItemGrid();
					isAdding = false;
				}
			}
		});
	}


	private void updateCollectItemGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			collectItemGridAdapter.add(item);
		}
	}


	private void addNextCollectItemGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			collectItemGridAdapter.add(item);
		}
	}
}
