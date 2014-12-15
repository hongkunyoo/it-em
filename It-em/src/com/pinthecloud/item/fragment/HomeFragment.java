package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.HomeItemListAdapter;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.Item;

public class HomeFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mListRefresh;
	private Button mUploadButton;

	private RecyclerView mListView;
	private HomeItemListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private boolean mIsAdding = false;
	private int page = 0;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		setActionBar();
		findComponent(view);
		setButton();
		setRefreshLayout();
		setList();
		updateList();
		return view;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.home));
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.home_frag_progress_bar);
		mListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.home_frag_item_list_refresh);
		mUploadButton = (Button)view.findViewById(R.id.home_frag_upload_button);
		mListView = (RecyclerView)view.findViewById(R.id.home_frag_item_list);
	}


	private void setButton(){
		mUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, UploadActivity.class);
				startActivity(intent);
			}
		});
	}


	private void setRefreshLayout(){
		mListRefresh.setColorSchemeResources(R.color.accent_color);
		mListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateList();
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mListAdapter = new HomeItemListAdapter(mActivity, mThisFragment, mItemList);
		mListView.setAdapter(mListAdapter);

		mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int lastVisibleItem = mListLayoutManager.findLastVisibleItemPosition();
				int totalItemCount = mListLayoutManager.getItemCount();

				if (lastVisibleItem >= totalItemCount-3 && !mIsAdding) {
					addNextItemList();
				}
			}
		});
	}


	public void updateList() {
		mAimHelper.listItem(mThisFragment, 0, new ItListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mListRefresh.setRefreshing(false);
				mListRefresh.setVisibility(View.VISIBLE);

				mItemList.clear();
				mListAdapter.addAll(list);
			}
		});
	}


	private void addNextItemList() {
		mIsAdding = true;
		mListAdapter.setHasFooter(true);
		mListAdapter.notifyItemInserted(mItemList.size());

		mAimHelper.listItem(mThisFragment, ++page, new ItListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mIsAdding = false;
				mListAdapter.setHasFooter(false);
				mListAdapter.addAll(list);
			}
		});
	}
}
