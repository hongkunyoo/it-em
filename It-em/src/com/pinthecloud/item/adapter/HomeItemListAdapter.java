package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;

public class HomeItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		FOOTER
	}

	private ItFragment frag;
	private List<Item> itemList;


	public HomeItemListAdapter(ItFragment frag, List<Item> itemList) {
		this.frag = frag;
		this.itemList = itemList;
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView image;
		public TextView text;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_home_item_list_image);
			this.text = (TextView)view.findViewById(R.id.row_home_item_list_text);
		}
	}


	public static class FooterViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ProgressBar progressBar;

		public FooterViewHolder(View view) {
			super(view);
			this.view = view;
			this.progressBar = (ProgressBar)view.findViewById(R.id.row_home_item_list_footer_progress_bar);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.FOOTER.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item_list_footer, parent, false);
			viewHolder = new FooterViewHolder(view);
		}
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = itemList.get(position);
			NormalViewHolder viewHolder = (NormalViewHolder)holder;
			setNormalComponent(viewHolder, item);
		}else if(viewType == VIEW_TYPE.FOOTER.ordinal()){
			FooterViewHolder viewHolder = (FooterViewHolder)holder;
			setFooterComponent(viewHolder);
		}
	}


	@Override
	public int getItemCount() {
		return this.itemList.size()+1;
	}


	@Override
	public int getItemViewType(int position) {
		if (position < getItemCount()-1) {
			return VIEW_TYPE.NORMAL.ordinal();
		} else{
			return VIEW_TYPE.FOOTER.ordinal();
		}
	}


	private void setNormalComponent(NormalViewHolder holder, Item item){
		holder.text.setText(item.getContent());
	}


	private void setFooterComponent(FooterViewHolder holder){
	}
}
