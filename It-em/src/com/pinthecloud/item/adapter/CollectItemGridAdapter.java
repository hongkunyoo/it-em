package com.pinthecloud.item.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;

public class CollectItemGridAdapter extends ArrayAdapter<Item> {

	private Context mContext;
	private ItFragment mFrag;


	public CollectItemGridAdapter(Context context, ItFragment frag) {
		super(context, 0);
		this.mContext = context;
		this.mFrag = frag;
	}


	private static class ViewHolder {
		public View mView;
		public ImageView mImage;

		public ViewHolder(View view) {
			this.mView = view;
			this.mImage = (ImageView)view.findViewById(R.id.row_collect_item_grid_image);
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
			view = inflater.inflate(R.layout.row_collect_item_grid, parent, false);
		}
		return new ViewHolder(view);
	}


	private void onBindViewHolder(ViewHolder holder, Item item) {
	}
}
