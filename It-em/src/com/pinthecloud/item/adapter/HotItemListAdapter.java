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

	private Context context;
	private ItFragment frag;


	public HotItemListAdapter(Context context, ItFragment frag) {
		super(context, 0);
		this.context = context;
		this.frag = frag;
	}


	private static class ViewHolder {
		public View view;
		public ImageView image;
		public TextView text;

		public ViewHolder(View view) {
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_hot_item_list_image);
			this.text = (TextView)view.findViewById(R.id.row_hot_item_list_text);
		}
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = onCreateViewHolder(convertView, parent);

		Item item = getItem(position);
		if (item != null) {
			onBindViewHolder(viewHolder, item);
		}

		return viewHolder.view;
	}


	private ViewHolder onCreateViewHolder(View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_hot_item_list, parent, false);
		}
		return new ViewHolder(view);
	}


	private void onBindViewHolder(ViewHolder holder, Item item) {
		holder.text.setText(item.getContent());
	}


	private static class HeaderViewHolder {
		public View view;
		public TextView text;

		public HeaderViewHolder(View view) {
			this.view = view;
			this.text = (TextView)view.findViewById(R.id.row_hot_item_list_header_text);
		}
	}


	@Override 
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder viewHolder = onCreateHeaderViewHolder(convertView, parent);

		Item item = getItem(position);
		if (item != null) {
			onBindHeaderViewHolder(viewHolder, item);
		}

		return viewHolder.view;
	}


	private HeaderViewHolder onCreateHeaderViewHolder(View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_hot_item_list_header, parent, false);
		}
		return new HeaderViewHolder(view);
	}


	private void onBindHeaderViewHolder(HeaderViewHolder holder, Item item) {
		holder.text.setText(item.getContent());
	}


	@Override
	public long getHeaderId(int position) {
		return getItem(position).getContent().subSequence(0, 1).charAt(0);
	}
}
