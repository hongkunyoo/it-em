package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.pinthecloud.item.R;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.model.Item;

public class HotItemListHeaderAdapter implements StickyHeadersAdapter<HotItemListHeaderAdapter.ViewHolder> {

	private List<Item> mItemList;


	public HotItemListHeaderAdapter(List<Item> itemList) {
		this.mItemList = itemList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View mView;
		public TextView date;

		public ViewHolder(View view) {
			super(view);
			this.mView = view;
			this.date = (TextView)view.findViewById(R.id.row_hot_item_list_header_date);
		}
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hot_item_list_header, parent, false);
		return new ViewHolder(itemView);
	}


	@Override
	public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
		Item item = mItemList.get(position);
		headerViewHolder.date.setText(item.getCreateDateTime().toPrettyDateTime());
	}


	@Override
	public long getHeaderId(int position) {
		if (mItemList.size() <= position) {
			throw new ItException("DON'T KNOW WHY position is bigger than list size : " + position);
		}
		return Long.parseLong(mItemList.get(position).getCreateDateTime().toDate(), 10);
	}
}
