package com.pinthecloud.item.adapter;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.model.ProductTag;

public class ProductTagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		HEADER,
		NORMAL
	}

	private ItApplication mApp;
	private ItActivity mActivity;
	private Fragment mFrag;
	private List<ProductTag> mTagList;


	public ProductTagListAdapter(ItActivity activity, Fragment frag, List<ProductTag> tagList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mTagList = tagList;
	}


	public static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView category;

		public HeaderViewHolder(View view) {
			super(view);
			this.view = view;
			this.category = (TextView)view.findViewById(R.id.row_product_tag_header_category);
		}
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView name;
		public Button price;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.name = (TextView)view.findViewById(R.id.row_product_tag_name);
			this.price = (Button)view.findViewById(R.id.row_product_tag_price);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.HEADER.ordinal()){
			view = inflater.inflate(R.layout.row_product_tag_list_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_product_tag_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		} 

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ProductTag tag = mTagList.get(position);
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.HEADER.ordinal()){
			HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
			headerViewHolder.category.setText(tag.categoryString(mActivity));
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalComponent(normalViewHolder, tag);
		}
	}


	@Override
	public int getItemCount() {
		return mTagList.size();
	}


	@Override
	public int getItemViewType(int position) {
		ProductTag tag = mTagList.get(position);
		if (tag.getShopName() == null) {
			return VIEW_TYPE.HEADER.ordinal();
		} else{
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setNormalComponent(NormalViewHolder holder, final ProductTag tag){
		holder.name.setText(tag.getShopName());

		double price = tag.getPrice();
		String priceString = null;
		if((price*100)%100 > 0){
			price = Math.floor(price*100)/100;
			tag.setPrice(price);
			priceString = String.format(Locale.US, "%,d", price);
		} else {
			priceString = String.format(Locale.US, "%,d", (int)price);
		}
		holder.price.setText(String.format(mActivity.getResources().getString(R.string.price_kor), priceString));

		holder.price.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.PRICE, GAHelper.PRODUCT_TAG);

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tag.getWebPage()));
				mActivity.startActivity(intent);
			}
		});
	}


	public void addAll(List<ProductTag> tagList) {
		mTagList.addAll(tagList);
		notifyDataSetChanged();
	}
}
