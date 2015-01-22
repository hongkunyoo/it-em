package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.ProductTag;

public class ProductTagListAdapter extends RecyclerView.Adapter<ProductTagListAdapter.ViewHolder> {

	private List<ProductTag> mTagList;


	public ProductTagListAdapter(List<ProductTag> tagList) {
		this.mTagList = tagList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
		}
	}


	@Override
	public ProductTagListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_tag_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ProductTag tag = mTagList.get(position);
	}


	@Override
	public int getItemCount() {
		return mTagList.size();
	}


	public void addAll(List<ProductTag> tagList) {
		mTagList.addAll(tagList);
		notifyDataSetChanged();
	}
}
