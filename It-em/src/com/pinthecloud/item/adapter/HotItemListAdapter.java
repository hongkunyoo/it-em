package com.pinthecloud.item.adapter;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;

public class HotItemListAdapter extends ArrayAdapter<Item> implements StickyListHeadersAdapter {

	private Context mContext;
	private ItFragment mFrag;


	public HotItemListAdapter(Context context, ItFragment frag) {
		super(context, 0);
		this.mContext = context;
		this.mFrag = frag;
	}


	private static class ViewHolder {
		public View mView;
		public ImageView mImage;
		public TextView mText;

		public ViewHolder(View view) {
			this.mView = view;
			this.mImage = (ImageView)view.findViewById(R.id.row_hot_item_list_image);
			this.mText = (TextView)view.findViewById(R.id.row_hot_item_list_text);
		}
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = onCreateViewHolder(convertView, parent);
		Item item = getItem(position);
		if (item != null) {
			onBindViewHolder(viewHolder, item);
		}
		return viewHolder.mView;
	}


	private ViewHolder onCreateViewHolder(View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_hot_item_list, parent, false);
		}
		return new ViewHolder(view);
	}


	private void onBindViewHolder(ViewHolder holder, Item item) {
		holder.mText.setText(item.getContent());
	}


	private static class HeaderViewHolder {
		public View mView;
		public TextView mText;

		public HeaderViewHolder(View view) {
			this.mView = view;
			this.mText = (TextView)view.findViewById(R.id.row_hot_item_list_header_text);
		}
	}


	@Override 
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder viewHolder = onCreateHeaderViewHolder(convertView, parent);
		Item item = getItem(position);
		if (item != null) {
			onBindHeaderViewHolder(viewHolder, item);
		}
		return viewHolder.mView;
	}


	private HeaderViewHolder onCreateHeaderViewHolder(View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_hot_item_list_header, parent, false);
		}
		return new HeaderViewHolder(view);
	}


	private void onBindHeaderViewHolder(HeaderViewHolder holder, Item item) {
		holder.mText.setText(item.getContent());
	}


	@Override
	public long getHeaderId(int position) {
		return getItem(position).getContent().subSequence(0, 1).charAt(0);
	}
}
