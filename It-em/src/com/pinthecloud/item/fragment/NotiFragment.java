package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.NotiListAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.NotiRecord;

public class NotiFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mRefresh;
	private RecyclerView mListView;
	private NotiListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<NotiRecord> mNotiList;

	private ItUser mMyItUser;
	private boolean mIsAdding = false;
	private int page = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_noti, container, false);
		findComponent(view);
		setRefreshLayout();
		setList();
		setScroll();

		mProgressBar.setVisibility(View.VISIBLE);
		mRefresh.setVisibility(View.GONE);
		updateList();

		return view;
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.noti_frag_refresh);
		mListView = (RecyclerView)view.findViewById(R.id.noti_frag_list);
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
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mNotiList = new ArrayList<NotiRecord>();
		mListAdapter = new NotiListAdapter(mNotiList);
		mListView.setAdapter(mListAdapter);
	}


	private void setScroll(){
		mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				// Add more items when grid reaches bottom
				int position = mListLayoutManager.findLastVisibleItemPosition();
				int totalItemCount = mListLayoutManager.getItemCount();
				if (position >= totalItemCount-2 && !mIsAdding) {
					addNextItem();
				}
			}
		});
	}


	private void updateList(){
		page = 0;
		mAimHelper.listMyNoti(page, mMyItUser.getId(), new ListCallback<NotiRecord>() {

			@Override
			public void onCompleted(List<NotiRecord> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mRefresh.setVisibility(View.VISIBLE);
				mRefresh.setRefreshing(false);

				mNotiList.clear();
				mListAdapter.addAll(list);
			}
		});
	}


	private void addNextItem() {
		mIsAdding = true;
		mAimHelper.listMyNoti(++page, mMyItUser.getId(), new ListCallback<NotiRecord>() {

			@Override
			public void onCompleted(List<NotiRecord> list, int count) {
				mIsAdding = false;
				mListAdapter.addAll(list);
			}
		});
	}
}
