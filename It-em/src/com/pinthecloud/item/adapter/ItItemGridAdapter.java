package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.SquareImageView;
import com.squareup.picasso.Picasso;

public class ItItemGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		HEADER
	}

	private Context mContext;
	private List<Item> mItemList;
	private int mGridColumnNum;


	public ItItemGridAdapter(Context context, List<Item> itemList) {
		this.mContext = context;
		this.mItemList = itemList;
		this.mGridColumnNum = mContext.getResources().getInteger(R.integer.it_user_page_item_grid_column_num);
	}


	private static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public SquareImageView image;
		public TextView itNumber;
		public TextView reply;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (SquareImageView)view.findViewById(R.id.row_it_item_grid_image);
			this.itNumber = (TextView)view.findViewById(R.id.row_it_item_grid_it_number);
			this.reply = (TextView)view.findViewById(R.id.row_it_item_grid_reply);
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
			view = inflater.inflate(R.layout.row_it_item_grid, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.HEADER.ordinal()){
			view = inflater.inflate(R.layout.row_my_item_grid_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(position-mGridColumnNum);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalText(normalViewHolder, item);
			setNormalButton(normalViewHolder, item);
			setNormalImageView(normalViewHolder, item);
		}
	}


	@Override
	public int getItemCount() {
		return mItemList.size()+mGridColumnNum;
	}


	@Override
	public int getItemViewType(int position) {
		if (position < mGridColumnNum) {
			return VIEW_TYPE.HEADER.ordinal();
		} else{
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setNormalText(NormalViewHolder holder, Item item){
		holder.itNumber.setText(""+item.getLikeItCount());
		holder.reply.setText(""+item.getReplyCount());
	}


	private void setNormalButton(NormalViewHolder holder, final Item item){
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mContext.startActivity(intent);
			}
		});
	}


	private void setNormalImageView(NormalViewHolder holder, Item item) {
		Picasso.with(holder.image.getContext())
		.load(BlobStorageHelper.getItemImgUrl(item.getId()))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(holder.image);
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}
}
