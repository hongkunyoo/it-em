package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.pinthecloud.item.R;
import com.pinthecloud.item.model.ItDateTime;
import com.pinthecloud.item.model.Item;

public class HotItemListHeaderAdapter implements StickyHeadersAdapter<HotItemListHeaderAdapter.ViewHolder> {

	private Context mContext;
	private List<Item> mItemList;


	public HotItemListHeaderAdapter(Context context, List<Item> itemList) {
		this.mContext = context;
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
		ItDateTime itDateTime = item.getCreateDateTime();

		switch(itDateTime.getElapsedDate()){
		case 0:
			headerViewHolder.date.setText(mContext.getResources().getString(R.string.today));
			break;
		case 1:
			headerViewHolder.date.setText(mContext.getResources().getString(R.string.yesterday));
			break;
		default:
			headerViewHolder.date.setText(item.getCreateDateTime().toPrettyDate());
			break;
		}
	}


	@Override
	public long getHeaderId(int position) {
		String date = null;
		if(position < mItemList.size()){
			date = mItemList.get(position).getCreateDateTime().toDate();
		} else {
			date = mItemList.get(mItemList.size()-1).getCreateDateTime().toDate();
		}
		return Long.parseLong(date, 10);
	}
}
