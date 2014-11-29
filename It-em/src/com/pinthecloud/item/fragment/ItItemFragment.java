package com.pinthecloud.item.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ItItemGridAdapter;
import com.pinthecloud.item.model.Item;

public class ItItemFragment extends MyPageItemFragment {

	private RecyclerView mGridView;
	private ItItemGridAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;
	private boolean mIsAdding = false;


	public static MyPageItemFragment newInstance(int position) {
		ItItemFragment fragment = new ItItemFragment();
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
		View view = inflater.inflate(R.layout.fragment_it_item, container, false);
		findComponent(view);
		setGrid(inflater);
		updateGrid();
		return view;
	}


	@Override
	public void adjustScroll(final int scrollHeight) {
		int findFirstVisibleItemPosition = mGridLayoutManager.findFirstVisibleItemPosition();
		int spanCount = mGridLayoutManager.getSpanCount();
		if (scrollHeight - MyPageFragment.mTabHeight != 0 || findFirstVisibleItemPosition < spanCount) {
			mGridLayoutManager.scrollToPositionWithOffset(spanCount, scrollHeight);
		}
	}


	private void findComponent(View view){
		mGridView = (RecyclerView)view.findViewById(R.id.it_item_frag_grid);
	}


	private void setGrid(LayoutInflater inflater){
		mGridView.setHasFixedSize(true);

		mGridLayoutManager = new GridLayoutManager(mActivity, getResources().getInteger(R.integer.my_page_item_grid_column_num));
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mItemList = Lists.newArrayList();
		mGridAdapter = new ItItemGridAdapter(mActivity, mThisFragment, mItemList);
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				// Scroll Header
				if (mScrollTabHolder != null){
					mScrollTabHolder.onScroll(recyclerView, mGridLayoutManager, mPosition);
				}

				// Add more item
				int lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
				int totalItemCount = mGridLayoutManager.getItemCount();
				if (lastVisibleItem >= totalItemCount-3 && !mIsAdding) {
					addNextItemList();
				}
			}
		});
	}


	private void updateGrid() {
		mGridAdapter.notifyDataSetChanged();
	}


	private void addNextItemList() {
		mIsAdding = true;

		mIsAdding = false;
		mGridAdapter.notifyDataSetChanged();
	}
}