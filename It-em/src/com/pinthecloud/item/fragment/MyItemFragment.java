package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.GridViewWithHeaderFooter;

public class MyItemFragment extends MyPageItemFragment {

	private GridViewWithHeaderFooter mGridView;
	private MyItemGridAdapter mGridAdapter;
	private int mGridSpacing;
	private boolean mIsAdding = false;


	public static MyPageItemFragment newInstance(int position) {
		MyItemFragment fragment = new MyItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(POSITION_KEY);
		mGridSpacing = getResources().getDimensionPixelSize(R.dimen.my_item_spacing);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);
		findComponent(view);
		setGrid(inflater);
		updateGrid();
		return view;
	}


	@Override
	public void adjustScroll(final int scrollHeight) {
		if (scrollHeight - MyPageFragment.mTabHeight != 0 || mGridView.getFirstVisiblePosition() < 1) {
			mGridView.smoothScrollToPositionFromTop(mGridView.getNumColumns(), scrollHeight+mGridSpacing, 0);
		}
	}


	private void findComponent(View view){
		mGridView = (GridViewWithHeaderFooter)view.findViewById(R.id.my_item_frag_grid);
	}


	private void setGrid(LayoutInflater inflater){
		View header = inflater.inflate(R.layout.row_my_item_grid_header, mGridView, false);
		mGridView.addHeaderView(header);
		
		mGridAdapter = new MyItemGridAdapter(mActivity, mThisFragment);
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		mGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mScrollTabHolder != null){
					// Scroll Header
					mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
				}

				if (firstVisibleItem + visibleItemCount >= totalItemCount-6 && !mIsAdding) {
					// Add more item
					mIsAdding = true;
					addNextItem();
					mIsAdding = false;
				}
			}
		});
	}


	public void updateGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			mGridAdapter.add(item);
		}
	}


	private void addNextItem() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			mGridAdapter.add(item);
		}
	}
}
