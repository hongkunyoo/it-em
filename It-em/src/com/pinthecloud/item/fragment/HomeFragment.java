package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.HomeItemListAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;

public class HomeFragment extends MainTabFragment {

	private final int UPLOAD = 0;

	private ProgressBar mProgressBar;
	private View mLayout;
	private SwipeRefreshLayout mRefresh;
	private RecyclerView mListView;
	private HomeItemListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private View mUploadLayout;
	private ImageButton mUploadButton;

	private boolean mIsAdding = false;
	private int page = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		findComponent(view);
		setComponent();
		updateProfile();
		setRefreshLayout();
		setList();
		setScroll();

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			switch(requestCode){
			case UPLOAD:
				Item item = data.getParcelableExtra(Item.INTENT_KEY);
				mListAdapter.add(0, item);
				mListView.smoothScrollToPosition(0);
				break;
			}
		}
	}


	@Override
	public void updateFragment() {
		mGaHelper.sendScreen(mThisFragment);

		mProgressBar.setVisibility(View.VISIBLE);
		mLayout.setVisibility(View.GONE);
		updateGrid();
	}


	@Override
	public void updateProfile() {
		ItUser user = mObjectPrefHelper.get(ItUser.class);
		mUploadLayout.setVisibility(user.checkPro() ? View.VISIBLE : View.GONE);
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mLayout = view.findViewById(R.id.home_frag_layout);
		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.home_frag_refresh);
		mUploadLayout = view.findViewById(R.id.home_frag_upload_layout);
		mUploadButton = (ImageButton)view.findViewById(R.id.home_frag_upload_button);
		mListView = (RecyclerView)view.findViewById(R.id.home_frag_item_list);
	}


	private void setComponent(){
		int uploadButtonHeight = BitmapFactory.decodeResource(getResources(), R.drawable.feed_upload_btn).getHeight();
		int uploadLayoutHeight = uploadButtonHeight + getResources().getDimensionPixelSize(R.dimen.key_line_first);
		mUploadLayout.getLayoutParams().height = uploadLayoutHeight;

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
				updateGrid();
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
	}


	private void setScroll(){
		final int maxUploadScrollY = mUploadLayout.getLayoutParams().height;
		mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				// Add more items when list reaches bottom
				int position = mListLayoutManager.findLastVisibleItemPosition();
				int totalItemCount = mListLayoutManager.getItemCount();
				if (position >= totalItemCount-3 && !mIsAdding) {
					addNextItem();
				}

				// Scroll upload button by dy
				if(dy < 0){
					// Scroll Up, Upload button Up
					mUploadLayout.scrollTo(0, Math.min(mUploadLayout.getScrollY()-dy, 0));
				} else if(dy > 0) {
					// Scroll down, Upload button Down
					mUploadLayout.scrollTo(0, Math.max(mUploadLayout.getScrollY()-dy, -maxUploadScrollY));
				}
			}
		});
	}


	public void updateGrid() {
		page = 0;

		ItUser user = mObjectPrefHelper.get(ItUser.class);
		mAimHelper.listItem(page, user.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mLayout.setVisibility(View.VISIBLE);
				mRefresh.setRefreshing(false);

				mItemList.clear();
				mListAdapter.addAll(list);
			}
		});
	}


	private void addNextItem() {
		mIsAdding = true;

		ItUser user = mObjectPrefHelper.get(ItUser.class);
		mAimHelper.listItem(++page, user.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mIsAdding = false;
				mListAdapter.addAll(list);
			}
		});
	}
}
