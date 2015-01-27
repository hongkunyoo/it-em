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
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ProductTagListAdapter;
import com.pinthecloud.item.model.ProductTag;
import com.pinthecloud.item.util.ViewUtil;

public class ProductTagDialog extends ItDialogFragment {

	private TextView mTitle;

	private RecyclerView mListView;
	private ProductTagListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<ProductTag> mTagList;


	public static ProductTagDialog newInstance(ArrayList<ProductTag> tagList) {
		ProductTagDialog dialog = new ProductTagDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(ProductTag.INTENT_KEY, tagList);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTagList = getArguments().getParcelableArrayList(ProductTag.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_product_tag, container, false);
		findComponent(view);
		setComponent();
		setList();
		return view;
	}


	private void findComponent(View view){
		mTitle = (TextView)view.findViewById(R.id.product_tag_title);
		mListView = (RecyclerView)view.findViewById(R.id.product_tag_frag_list);
	}


	private void setComponent(){
		mTitle.setText(mTagList.get(0).categoryString(getResources()));
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mListAdapter = new ProductTagListAdapter(mActivity, mTagList);
		mListView.setAdapter(mListAdapter);
		
		ViewUtil.setListHeightBasedOnChildren(mListView, mTagList.size());
	}
}
