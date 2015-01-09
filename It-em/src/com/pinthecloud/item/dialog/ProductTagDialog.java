package com.pinthecloud.item.dialog;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ProductTagListAdapter;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;

public class ProductTagDialog extends ItDialogFragment {

	private ProgressBar mProgressBar;
	private RelativeLayout mListLayout;
	private TextView mListEmptyView;
	private RecyclerView mListView;
	private ProductTagListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private ItFragment mFrag;
	private Item mItem;


	public ProductTagDialog(ItFragment frag, Item item) {
		super();
		this.mFrag = frag;
		this.mItem = item;
		setStyle(STYLE_NO_TITLE, 0);
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
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mListLayout = (RelativeLayout)view.findViewById(R.id.product_tag_frag_list_layout);
		mListEmptyView = (TextView)view.findViewById(R.id.product_tag_frag_list_empty_view);
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
	}
}
