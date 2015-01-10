package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.HomeItemListAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.Item;

public class HomeFragment extends ItFragment {

	private final int UPLOAD = 0;

	private ProgressBar mProgressBar;
	private RelativeLayout mLayout;
	private SwipeRefreshLayout mRefresh;
	private Button mUploadButton;

	private RecyclerView mGridView;
	private HomeItemListAdapter mGridAdapter;
	private StaggeredGridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;

	private boolean mIsAdding = false;
	private int page = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(mItemList == null){
			mItemList = new ArrayList<Item>();
		}
	}


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

		if(mItemList.size() < 1){
			mProgressBar.setVisibility(View.VISIBLE);
			mLayout.setVisibility(View.GONE);
			updateList();
		}

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case UPLOAD:
			if (resultCode == Activity.RESULT_OK){
				Item item = data.getParcelableExtra(Item.INTENT_KEY);
				mGridAdapter.add(0, item);
				mGridView.smoothScrollToPosition(0);
			}
			break;
		}
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.home));
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mLayout = (RelativeLayout)view.findViewById(R.id.home_frag_layout);
		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.home_frag_item_list_refresh);
		mUploadButton = (Button)view.findViewById(R.id.home_frag_upload_button);
		mGridView = (RecyclerView)view.findViewById(R.id.home_frag_item_list);
	}


	private void setButton(){
		mUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, UploadActivity.class);
				startActivityForResult(intent, UPLOAD);
			}
		});
	}


	private void setRefreshLayout(){
		mRefresh.setColorSchemeResources(R.color.accent_color);
		mRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateList();
			}
		});
	}


	private void setList(){
		mGridView.setHasFixedSize(true);

		int gridColumnNum = getResources().getInteger(R.integer.home_grid_column_num);
		mGridLayoutManager = new StaggeredGridLayoutManager(gridColumnNum, StaggeredGridLayoutManager.VERTICAL);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mGridAdapter = new HomeItemListAdapter(mActivity, mThisFragment, mItemList);
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int[] positions = mGridLayoutManager.findLastVisibleItemPositions(null);
				int totalItemCount = mGridLayoutManager.getItemCount();

				if (positions[0] >= totalItemCount-1 && !mIsAdding) {
					addNextItemList();
				}
			}
		});
	}


	public void updateList() {
		page = 0;
		mAimHelper.listItem(page, new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mLayout.setVisibility(View.VISIBLE);
				mRefresh.setRefreshing(false);

				mItemList.clear();
				mGridAdapter.addAll(list);
			}
		});
	}


	private void addNextItemList() {
		mIsAdding = true;
		mAimHelper.listItem(++page, new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mIsAdding = false;
				mGridAdapter.addAll(list);
			}
		});
	}
}
