package com.pinthecloud.item.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.UploadFragment;
import com.pinthecloud.item.interfaces.DialogCallback;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

	private List<String> mCategoryList;
	private DialogCallback mCallback;
	

	public CategoryListAdapter(Context context, DialogCallback callback) {
		this.mCategoryList = Arrays.asList(context.getResources().getStringArray(R.array.category_array));
		this.mCallback = callback;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView content;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.content = (TextView)view.findViewById(R.id.row_category_content);
		}
	}


	@Override
	public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final String category = mCategoryList.get(position);
		holder.content.setText(category);
		holder.view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(UploadFragment.CATEGORY_INTENT_KEY, category);
				mCallback.doPositive(bundle);
			}
		});
	}


	@Override
	public int getItemCount() {
		return mCategoryList.size();
	}
}
