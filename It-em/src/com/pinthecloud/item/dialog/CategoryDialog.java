package com.pinthecloud.item.dialog;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.CategoryListAdapter;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.util.ViewUtil;

public class CategoryDialog extends ItDialogFragment {

	private RecyclerView mGridView;
	private CategoryListAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private DialogCallback mCallback;

	public void setCallback(DialogCallback mCallback) {
		this.mCallback = mCallback;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_category, container, false);

		mGaHelper.sendScreen(mThisFragment);
		findComponent(view);
		setGrid();

		return view;
	}


	private void findComponent(View view){
		mGridView = (RecyclerView)view.findViewById(R.id.category_frag_list);
	}


	private void setGrid(){
		mGridView.setHasFixedSize(true);

		final int gridColumnNum = getResources().getInteger(R.integer.category_grid_column_num);
		mGridLayoutManager = new GridLayoutManager(mActivity, gridColumnNum);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mGridAdapter = new CategoryListAdapter(mActivity, mCallback);
		mGridView.setAdapter(mGridAdapter);

		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				int rowCount = (int)Math.ceil((double)mGridAdapter.getItemCount()/gridColumnNum);
				ViewUtil.setListHeightBasedOnChildren(mGridView, rowCount);
			}
		});
	}
}
