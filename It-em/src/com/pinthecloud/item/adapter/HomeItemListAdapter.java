package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.ViewHolder> {

	private ItFragment frag;
	private List<Item> itemList;


	public HomeItemListAdapter(ItFragment frag, List<Item> itemList) {
		this.frag = frag;
		this.itemList = itemList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView image;
		public TextView text;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_home_item_list_image);
			this.text = (TextView)view.findViewById(R.id.row_home_item_list_text);
		}
	}


	@Override
	public HomeItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item_list, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Item item = itemList.get(position);
		holder.text.setText(item.getContent());
	}


	@Override
	public int getItemCount() {
		return this.itemList.size();
	}
}
