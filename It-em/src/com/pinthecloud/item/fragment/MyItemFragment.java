package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.interfaces.UserPageScrollTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ViewUtil;

public class MyItemFragment extends ItFragment implements UserPageScrollTabHolder {

	private static final String POSITION_KEY = "POSITION_KEY";
	private static final String HEADER_HEIGHT_KEY = "HEADER_HEIGHT_KEY";
	private static final String TAB_HEIGHT_KEY = "TAB_HEIGHT_KEY";
	
	private final int MY_ITEM = 0;
	private final int LIKE = 1;

	private View mLayout;
	private SwipeRefreshLayout mRefresh;
	private RecyclerView mGridView;
	private MyItemGridAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;

	private View mComponentLayout;
	private ProgressBar mProgressBar;
	private LinearLayout mGridEmptyLayout;
	private TextView mGridEmptyText;
	private ImageView mGridEmptyImage;

	private ItUser mUser;
	private int mPosition;
	private int mHeaderHeight;
	private int mTabHeight;

	private UserPageScrollTabHolder mScrollTabHolder;

	public void setScrollTabHolder(UserPageScrollTabHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}


	public static MyItemFragment newInstance(int position, ItUser user, int headerHeight, int tabHeight) {
		MyItemFragment fragment = new MyItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putParcelable(ItUser.INTENT_KEY, user);
		bundle.putInt(HEADER_HEIGHT_KEY, headerHeight);
		bundle.putInt(TAB_HEIGHT_KEY, tabHeight);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(POSITION_KEY);
		mUser = getArguments().getParcelable(ItUser.INTENT_KEY);
		mHeaderHeight = getArguments().getInt(HEADER_HEIGHT_KEY);
		mTabHeight = getArguments().getInt(TAB_HEIGHT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);

		mGaHelper.sendScreen(mThisFragment);

		findComponent(view);
		setComponent();
		setGrid();
		updateHeader(mHeaderHeight);
		setScroll();
		setRefreshLayout();

		mProgressBar.setVisibility(View.VISIBLE);
		mRefresh.setVisibility(View.GONE);
		updateGrid(false);

		return view;
	}
	
	
	@Override
	public void adjustScroll(final int scrollHeight) {
		if (scrollHeight - mTabHeight != 0 || mGridLayoutManager.findFirstVisibleItemPosition() < mGridLayoutManager.getSpanCount()) {
			mGridLayoutManager.scrollToPositionWithOffset(mGridLayoutManager.getSpanCount(), scrollHeight);
			onScrollTabHolder();
		}
	}


	@Override
	public void updateHeader(int headerHeight) {
		mGridAdapter.notifyHeader(headerHeight);
		int height = ViewUtil.getActionBarHeight(mActivity)/2;
		mRefresh.setProgressViewOffset(true, headerHeight-height, headerHeight+height);

		mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				mComponentLayout.getLayoutParams().height = mLayout.getHeight() - mHeaderHeight;
				mComponentLayout.requestLayout();
			}
		});
	}


	@Override
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
	}


	@Override
	public void updateTabNumber(int position, int number) {
	}


	private void findComponent(View view){
		mLayout = view.findViewById(R.id.my_item_frag_layout);
		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.my_item_frag_refresh);
		mGridView = (RecyclerView)view.findViewById(R.id.my_item_frag_grid);

		mComponentLayout = view.findViewById(R.id.my_item_frag_component_layout);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mGridEmptyLayout = (LinearLayout)view.findViewById(R.id.my_item_frag_grid_empty_layout);
		mGridEmptyText = (TextView)view.findViewById(R.id.my_item_frag_grid_empty_text);
		mGridEmptyImage = (ImageView)view.findViewById(R.id.my_item_frag_grid_empty_image);
	}


	private void setComponent(){
		if(mPosition == MY_ITEM){
			mGridEmptyImage.setImageResource(R.drawable.mypage_item_empty_ic);
			mGridEmptyText.setText(getResources().getString(R.string.empty_my_item));
		} else if(mPosition == LIKE) {
			mGridEmptyImage.setImageResource(R.drawable.mypage_like_empty_ic);
			mGridEmptyText.setText(getResources().getString(R.string.empty_like_item));
		}
	}


	private void setGrid(){
		mGridView.setHasFixedSize(true);

		int gridColumnNum = getResources().getInteger(R.integer.my_item_grid_column_num);
		mGridLayoutManager = new GridLayoutManager(mActivity, gridColumnNum);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mGridAdapter = new MyItemGridAdapter(mActivity, mThisFragment, gridColumnNum, mItemList);
		mGridView.setAdapter(mGridAdapter);
	}


	private void setScroll(){
		mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				onScrollTabHolder();
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


	private void updateGrid(boolean refresh) {
		if(mPosition == MY_ITEM){
			updateMyItemGrid(refresh);
		} else if(mPosition == LIKE) {
			updateItItemGrid(refresh);
		}
	}


	private void updateMyItemGrid(final boolean refresh){
		mAimHelper.listMyItem(mUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(isAdded()){
					setGridItem(list, count, refresh);
				}
			}
		});
	}


	private void updateItItemGrid(final boolean refresh){
		mAimHelper.listItItem(mUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(isAdded()){
					setGridItem(list, count, refresh);
				}
			}
		});
	}


	private void setGridItem(List<Item> list, int count, boolean refresh){
		if(refresh){
			mRefresh.setRefreshing(false);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
		}

		mItemList.clear();
		mGridAdapter.addAll(list);
		mGridView.scrollToPosition(0);

		if(mScrollTabHolder != null){
			mScrollTabHolder.updateTabNumber(mPosition, mItemList.size());	
		}

		mGridEmptyLayout.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
	}


	private void onScrollTabHolder(){
		if (mScrollTabHolder != null){
			mScrollTabHolder.onScroll(mGridView, mGridLayoutManager, mPosition);
		}
	}
}
