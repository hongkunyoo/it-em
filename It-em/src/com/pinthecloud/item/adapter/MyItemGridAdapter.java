package com.pinthecloud.item.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.SquareImageView;

public class MyItemGridAdapter extends ArrayAdapter<Item> {

	private Context mContext;
	private ItFragment mFrag;


	public MyItemGridAdapter(Context context, ItFragment frag) {
		super(context, 0);
		this.mContext = context;
		this.mFrag = frag;
	}


	private static class ViewHolder {
		public View view;
		public TextView rank;
		public SquareImageView image;
		public TextView itNumber;
		public TextView reply;

		public ViewHolder(View view) {
			this.view = view;
			this.rank = (TextView)view.findViewById(R.id.row_my_item_grid_rank);
			this.image = (SquareImageView)view.findViewById(R.id.row_my_item_grid_image);
			this.itNumber = (TextView)view.findViewById(R.id.row_my_item_grid_it_number);
			this.reply = (TextView)view.findViewById(R.id.row_my_item_grid_reply);
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
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_my_item_grid, parent, false);
		}
		return new ViewHolder(view);
	}


	private void onBindViewHolder(ViewHolder holder, Item item) {
		holder.itNumber.setText(item.getLikeItCount());
		holder.reply.setText(item.getReplyCount());
	}
}
