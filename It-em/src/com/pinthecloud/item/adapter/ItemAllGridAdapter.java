package com.pinthecloud.item.adapter;

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

public class ItemAllGridAdapter extends ArrayAdapter<Item> {

	private Context context;
	private ItFragment frag;


	public ItemAllGridAdapter(Context context, ItFragment frag) {
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
			this.image = (ImageView)view.findViewById(R.id.row_item_all_grid_image);
			this.text = (TextView)view.findViewById(R.id.row_item_all_grid_text);
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
			view = inflater.inflate(R.layout.row_item_all_grid, parent, false);
		}
		return new ViewHolder(view);
	}


	private void onBindViewHolder(ViewHolder holder, Item item) {
		holder.text.setText(item.getContent());
	}
}
