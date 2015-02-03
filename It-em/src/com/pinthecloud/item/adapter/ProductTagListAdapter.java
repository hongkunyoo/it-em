package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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

public class ProductTagListAdapter extends RecyclerView.Adapter<ProductTagListAdapter.ViewHolder> {

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


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView name;
		public Button price;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.name = (TextView)view.findViewById(R.id.row_product_tag_name);
			this.price = (Button)view.findViewById(R.id.row_product_tag_price);
		}
	}


	@Override
	public ProductTagListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_tag_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ProductTag tag = mTagList.get(position);
		setComponent(holder, tag);
	}


	@Override
	public int getItemCount() {
		return mTagList.size();
	}


	private void setComponent(ViewHolder holder, final ProductTag tag){
		holder.name.setText(tag.getShopName());
		double price = tag.getPrice();
		if((price*100)%100 > 0){
			holder.price.setText(price + mActivity.getResources().getString(R.string.price_unit));
		} else {
			holder.price.setText((int)price + mActivity.getResources().getString(R.string.price_unit));	
		}

		holder.price.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEventGA(
						mFrag.getClass().getSimpleName(), GAHelper.PRICE, GAHelper.ITEM);

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
