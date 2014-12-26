package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;

public class MyItemFragment extends ItUserPageScrollTabFragment {

	private ProgressBar mProgressBar;
	private RecyclerView mGridView;
	private MyItemGridAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;
	private boolean mIsAdding = false;


	public static ItUserPageScrollTabFragment newInstance(int position, ItUser itUser) {
		MyItemFragment fragment = new MyItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putParcelable(ItUser.INTENT_KEY, itUser);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(POSITION_KEY);
		mItUser = getArguments().getParcelable(ItUser.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);
		findComponent(view);
		setGrid(inflater);
		return view;
	}


	@Override
	public void adjustScroll(final int scrollHeight) {
		if (scrollHeight - ItUserPageFragment.mTabHeight != 0 || mGridLayoutManager.findFirstVisibleItemPosition() < mGridLayoutManager.getSpanCount()) {
			mGridLayoutManager.scrollToPositionWithOffset(mGridLayoutManager.getSpanCount(), scrollHeight);
		}
	}


	@Override
	public void updateTab() {
		mProgressBar.setVisibility(View.VISIBLE);
		mGridView.setVisibility(View.GONE);
		
		mAimHelper.listMyItem(mThisFragment, mItUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mGridView.setVisibility(View.VISIBLE);

				mItemList.clear();
				mGridAdapter.addAll(list);
				mItUserPageScrollTabHolder.updateTabNumber(mPosition, mItemList.size());
			}
		});
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.my_item_frag_progress_bar);
		mGridView = (RecyclerView)view.findViewById(R.id.my_item_frag_grid);
	}


	private void setGrid(LayoutInflater inflater){
		mGridView.setHasFixedSize(true);

		mGridLayoutManager = new GridLayoutManager(mActivity, getResources().getInteger(R.integer.it_user_page_item_grid_column_num));
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mGridAdapter = new MyItemGridAdapter(mActivity, mItemList);
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				// Scroll Header
				if (mItUserPageScrollTabHolder != null){
					mItUserPageScrollTabHolder.onScroll(recyclerView, mGridLayoutManager, mPosition);
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


	private void addNextItemList() {
		mIsAdding = true;

		mIsAdding = false;
		mGridAdapter.notifyDataSetChanged();
	}
}
