package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.UploadFragment.BrandInfo;

public class BrandInfoListAdapter extends RecyclerView.Adapter<BrandInfoListAdapter.ViewHolder> {

	private List<BrandInfo> mBrandInfoList;

	public BrandInfoListAdapter(List<BrandInfo> brandInfoList) {
		this.mBrandInfoList = brandInfoList;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView category;
		public EditText brand;
		public ImageButton remove;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.category = (TextView)view.findViewById(R.id.row_brand_info_category);
			this.brand = (EditText)view.findViewById(R.id.row_brand_info_brand);
			this.remove = (ImageButton)view.findViewById(R.id.row_brand_info_remove);
		}
	}


	@Override
	public BrandInfoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_info_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		BrandInfo brandInfo = mBrandInfoList.get(position);
		setComponent(holder, brandInfo);
		setButton(holder, brandInfo);
	}


	@Override
	public int getItemCount() {
		return mBrandInfoList.size();
	}
	
	
	private void setComponent(ViewHolder holder, final BrandInfo brandInfo){
		holder.category.setText(brandInfo.getCategory());
		
		holder.brand.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				brandInfo.setBrand(s.toString());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	
	private void setButton(ViewHolder holder, final BrandInfo brandInfor){
		holder.remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				remove(brandInfor);
			}
		});
	}
	
	
	public void add(int position, BrandInfo brandInfo) {
		mBrandInfoList.add(position, brandInfo);
		notifyItemInserted(position);
	}
	
	
	public void remove(BrandInfo brandInfo) {
		int position = mBrandInfoList.indexOf(brandInfo);
		mBrandInfoList.remove(position);
		notifyItemRemoved(position);
	}
}
