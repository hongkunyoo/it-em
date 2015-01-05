package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.model.Item;

public class ProductTagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		HEADER
	}

	private List<Item> mItemList;


	public ProductTagListAdapter(List<Item> itemList) {
		this.mItemList = itemList;
	}


	private static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
		}
	}


	private static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public HeaderViewHolder(View view) {
			super(view);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_product_tag_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.HEADER.ordinal()){
			view = inflater.inflate(R.layout.row_product_tag_list_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(--position);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
		}
	}


	@Override
	public int getItemCount() {
		return mItemList.size()+1;
	}


	@Override
	public int getItemViewType(int position) {
		if (position < 1) {
			return VIEW_TYPE.HEADER.ordinal();
		} else{
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}
}
