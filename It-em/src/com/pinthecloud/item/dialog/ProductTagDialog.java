package com.pinthecloud.item.dialog;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ProductTagListAdapter;
import com.pinthecloud.item.model.Item;

public class ProductTagDialog extends CustomDialog {

	private ProgressBar mProgressBar;
	private RelativeLayout mListLayout;
	private TextView mListEmptyView;
	private RecyclerView mListView;
	private ProductTagListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private Item mItem;


	public static ProductTagDialog newInstance(Item item) {
		ProductTagDialog dialog = new ProductTagDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		dialog.setArguments(bundle);
		return dialog;
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
		View view = inflater.inflate(R.layout.dialog_product_tag, container, false);
		findComponent(view);
		setList();
		return view;
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
