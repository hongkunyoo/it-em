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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
	private RecyclerView mGridView;
	private HomeItemListAdapter mGridAdapter;
	private StaggeredGridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;

	private View mUploadLayout;
	private ImageButton mUploadButton;

	private ItUser mUser;
	private boolean mIsAdding = false;
	private int page = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = mObjectPrefHelper.get(ItUser.class);
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
				mGridAdapter.add(0, item);
				mGridView.smoothScrollToPosition(0);
				break;
			}
		}
	}


	@Override
	public void updateFragment() {
		mGaHelper.sendScreen(mThisFragment);

		mProgressBar.setVisibility(View.VISIBLE);
		mLayout.setVisibility(View.GONE);
		updateGrid(false);
	}


	@Override
	public void updateProfile() {
		mUser = mObjectPrefHelper.get(ItUser.class);
		mUploadLayout.setVisibility(mUser.checkPro() ? View.VISIBLE : View.GONE);
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mLayout = view.findViewById(R.id.home_frag_layout);
		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.home_frag_refresh);
		mUploadLayout = view.findViewById(R.id.home_frag_upload_layout);
		mUploadButton = (ImageButton)view.findViewById(R.id.home_frag_upload_button);
		mGridView = (RecyclerView)view.findViewById(R.id.home_frag_item_grid);
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
				updateGrid(true);
			}
		});
	}


	private void setList(){
		mGridView.setHasFixedSize(true);

		int gridColumnNum = getResources().getInteger(R.integer.home_item_grid_column_num);
		mGridLayoutManager = new StaggeredGridLayoutManager(gridColumnNum, StaggeredGridLayoutManager.VERTICAL);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mGridAdapter = new HomeItemListAdapter(mActivity, mThisFragment, mItemList);
		mGridView.setAdapter(mGridAdapter);
	}


	private void setScroll(){
		final int maxUploadScrollY = mUploadLayout.getLayoutParams().height;
		mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				// Add more items when grid reaches bottom
				int[] positions = mGridLayoutManager.findLastVisibleItemPositions(null);
				int totalItemCount = mGridLayoutManager.getItemCount();
				if (positions[positions.length-1] >= totalItemCount-3 && !mIsAdding) {
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


	public void updateGrid(final boolean refresh) {
		page = 0;
		mAimHelper.listItem(page, mUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(isAdded()){
					if(refresh){
						mRefresh.setRefreshing(false);
					} else {
						mProgressBar.setVisibility(View.GONE);
						mLayout.setVisibility(View.VISIBLE);
					}

					mItemList.clear();
					mGridAdapter.addAll(list);
				}
			}
		});
	}


	private void addNextItem() {
		mIsAdding = true;
		mAimHelper.listItem(++page, mUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(isAdded()){
					mIsAdding = false;
					mGridAdapter.addAll(list);
				}
			}
		});
	}
}
