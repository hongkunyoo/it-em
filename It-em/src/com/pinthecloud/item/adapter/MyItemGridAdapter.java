package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ImageUtil;
import com.squareup.picasso.Picasso;

public class MyItemGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		HEADER
	}

	private ItActivity mActivity;
	private List<Item> mItemList;
	private int mGridColumnNum;


	public MyItemGridAdapter(ItActivity activity, int gridColumnNum, List<Item> itemList) {
		this.mActivity = activity;
		this.mItemList = itemList;
		this.mGridColumnNum = gridColumnNum;
	}


	private static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView itemImage;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.itemImage = (ImageView)view.findViewById(R.id.row_my_item_grid_item_image);
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
			view = inflater.inflate(R.layout.row_my_item_grid, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.HEADER.ordinal()){
			view = inflater.inflate(R.layout.row_my_item_grid_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(position-mGridColumnNum);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
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


	private void setNormalButton(final NormalViewHolder holder, final Item item){
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});
	}


	private void setNormalImageView(NormalViewHolder holder, Item item) {
		Picasso.with(holder.itemImage.getContext())
		.load(BlobStorageHelper.getItemImgUrl(item.getId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX))
		.into(holder.itemImage);
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}
}
