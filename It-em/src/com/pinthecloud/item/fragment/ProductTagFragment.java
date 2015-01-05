package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ProductTagListAdapter;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.SquareImageView;

public class ProductTagFragment extends ItFragment {

	private LinearLayout mHeader;
	private SquareImageView mImage;
	private ProgressBar mProgressBar;

	private RelativeLayout mListEmptyView;
	private RecyclerView mListView;
	private ProductTagListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private Item mItem;


	public static ProductTagFragment newInstance(Item item) {
		ProductTagFragment fragment = new ProductTagFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItem = getArguments().getParcelable(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_product_tag, container, false);
		setHasOptionsMenu(true);
		findComponent(view);
		setList();
		return view;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(menu);
	}


	private void findComponent(View view){
		mHeader = (LinearLayout)view.findViewById(R.id.product_tag_frag_header_layout);
		mImage = (SquareImageView)view.findViewById(R.id.product_tag_frag_image);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mListEmptyView = (RelativeLayout)view.findViewById(R.id.product_tag_frag_list_empty_view);
		mListView = (RecyclerView)view.findViewById(R.id.product_tag_frag_list);
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mListAdapter = new ProductTagListAdapter(mItemList);
		mListView.setAdapter(mListAdapter);

		mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int scrollY = getListScrollY(recyclerView, mListLayoutManager);
				mImage.scrollTo(0, scrollY/2);
			}
		});
	}


	private int getListScrollY(RecyclerView view, LinearLayoutManager layoutManager) {
		View c = view.getChildAt(0);
		if(c == null){
			return 0;
		}

		int findFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
		int headerHeight = 0;
		if (findFirstVisibleItemPosition >= 1) {
			headerHeight = mHeader.getHeight();
		}

		return -c.getTop() + (findFirstVisibleItemPosition) * c.getHeight() + headerHeight;
	}
}
