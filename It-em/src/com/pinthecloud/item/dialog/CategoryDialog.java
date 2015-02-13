package com.pinthecloud.item.dialog;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.CategoryListAdapter;
import com.pinthecloud.item.interfaces.DialogCallback;

public class CategoryDialog extends ItDialogFragment {

	private RecyclerView mListView;
	private CategoryListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private DialogCallback mCallback;

	public void setCallback(DialogCallback mCallback) {
		this.mCallback = mCallback;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_category, container, false);
		findComponent(view);
		setList();
		return view;
	}
	
	
	private void findComponent(View view){
		mListView = (RecyclerView)view.findViewById(R.id.category_frag_list);
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mListAdapter = new CategoryListAdapter(mActivity, mCallback);
		mListView.setAdapter(mListAdapter);
	}
}
