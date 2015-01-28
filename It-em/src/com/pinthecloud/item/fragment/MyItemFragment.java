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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.interfaces.ItUserPageScrollTabHolder;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;

public class MyItemFragment extends ItFragment implements ItUserPageScrollTabHolder {

	private static final String POSITION_KEY = "POSITION_KEY";
	private static final String HEADER_HEIGHT_KEY = "HEADER_HEIGHT_KEY";
	private static final String TAB_HEIGHT_KEY = "TAB_HEIGHT_KEY";

	private int MY_ITEM;
	private int IT_ITEM;

	private ProgressBar mProgressBar;
	private RelativeLayout mGridLayout;

	private LinearLayout mGridEmptyLayout;
	private TextView mGridEmptyText;
	private ImageView mGridEmptyImage;

	private RecyclerView mGridView;
	private MyItemGridAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;

	private ItUser mItUser;
	private int mPosition;
	private int mHeaderHeight;
	private int mTabHeight;


	private  ItUserPageScrollTabHolder mItUserPageScrollTabHolder;

	public void setItUserPageScrollTabHolder(ItUserPageScrollTabHolder itUserPageScrollTabHolder) {
		mItUserPageScrollTabHolder = itUserPageScrollTabHolder;
	}


	public static MyItemFragment newInstance(int position, ItUser itUser, int headerHeight, int tabHeight) {
		MyItemFragment fragment = new MyItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putParcelable(ItUser.INTENT_KEY, itUser);
		bundle.putInt(HEADER_HEIGHT_KEY, headerHeight);
		bundle.putInt(TAB_HEIGHT_KEY, tabHeight);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(POSITION_KEY);
		mItUser = getArguments().getParcelable(ItUser.INTENT_KEY);
		mHeaderHeight = getArguments().getInt(HEADER_HEIGHT_KEY);
		mTabHeight = getArguments().getInt(TAB_HEIGHT_KEY);

		if(mItUser.isPro()){
			MY_ITEM = 0;
			IT_ITEM = 1;
		} else {
			MY_ITEM = -1;
			IT_ITEM = 0;
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);
		findComponent(view);
		setComponent();
		setGrid();
		setScroll();
		updateGrid();
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
	}
	
	
	@Override
	public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
	}


	@Override
	public void updateTabNumber(int position, int number) {
	}
	

	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mGridLayout = (RelativeLayout)view.findViewById(R.id.my_item_frag_grid_layout);
		mGridEmptyLayout = (LinearLayout)view.findViewById(R.id.my_item_frag_grid_empty_layout);
		mGridEmptyText = (TextView)view.findViewById(R.id.my_item_frag_grid_empty_text);
		mGridEmptyImage = (ImageView)view.findViewById(R.id.my_item_frag_grid_empty_image);
		mGridView = (RecyclerView)view.findViewById(R.id.my_item_frag_grid);
	}


	private void setComponent(){
		if(mPosition == MY_ITEM){
			mGridEmptyImage.setImageResource(R.drawable.mypage_item_empty_ic);
			mGridEmptyText.setText(getResources().getString(R.string.empty_my_item));
		} else if(mPosition == IT_ITEM) {
			mGridEmptyImage.setImageResource(R.drawable.mypage_it_empty_ic);
			mGridEmptyText.setText(getResources().getString(R.string.empty_it_item));
		}
	}


	private void setGrid(){
		mGridView.setHasFixedSize(true);

		int gridColumnNum = getResources().getInteger(R.integer.my_item_grid_column_num);
		mGridLayoutManager = new GridLayoutManager(mActivity, gridColumnNum);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mGridAdapter = new MyItemGridAdapter(mActivity, gridColumnNum, mHeaderHeight, mItemList);
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
	
	
	private void updateGrid() {
		mProgressBar.setVisibility(View.VISIBLE);
		mGridLayout.setVisibility(View.GONE);

		if(mPosition == MY_ITEM){
			updateMyItemGrid();
		} else if(mPosition == IT_ITEM) {
			updateItItemGrid();
		}
	}


	private void updateMyItemGrid(){
		mAimHelper.listMyItem(mItUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(isAdded()){
					setGridItem(list, count);
				}
			}
		});
	}


	private void updateItItemGrid(){
		mAimHelper.listItItem(mItUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(isAdded()){
					setGridItem(list, count);
				}
			}
		});
	}


	private void setGridItem(List<Item> list, int count){
		mProgressBar.setVisibility(View.GONE);
		mGridLayout.setVisibility(View.VISIBLE);
		showGrid(count);

		mItemList.clear();
		mGridAdapter.addAll(list);
		mGridView.scrollToPosition(0);
		mItUserPageScrollTabHolder.updateTabNumber(mPosition, mItemList.size());
	}


	private void showGrid(int count){
		if(count <= 0){
			mGridEmptyLayout.setVisibility(View.VISIBLE);
			mGridView.setVisibility(View.GONE);
		} else {
			mGridEmptyLayout.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
		}
	}


	private void onScrollTabHolder(){
		if (mItUserPageScrollTabHolder != null){
			mItUserPageScrollTabHolder.onScroll(mGridView, mGridLayoutManager, mPosition);
		}
	}
}
