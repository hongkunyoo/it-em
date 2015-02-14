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
import com.pinthecloud.item.fragment.UploadFragment.BrandInformation;

public class BrandInformationListAdapter extends RecyclerView.Adapter<BrandInformationListAdapter.ViewHolder> {

	private List<BrandInformation> mBrandInformationList;


	public BrandInformationListAdapter(List<BrandInformation> brandInformationList) {
		this.mBrandInformationList = brandInformationList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView category;
		public EditText brand;
		public ImageButton remove;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.category = (TextView)view.findViewById(R.id.row_brand_information_category);
			this.brand = (EditText)view.findViewById(R.id.row_brand_information_brand);
			this.remove = (ImageButton)view.findViewById(R.id.row_brand_information_remove);
		}
	}


	@Override
	public BrandInformationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_information_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		BrandInformation brandInformation = mBrandInformationList.get(position);
		setComponent(holder, brandInformation);
		setButton(holder, brandInformation);
	}


	@Override
	public int getItemCount() {
		return mBrandInformationList.size();
	}
	
	
	private void setComponent(ViewHolder holder, BrandInformation brandInformation){
		holder.category.setText(brandInformation.getCategory());
		
		holder.brand.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
	
	
	private void setButton(ViewHolder holder, final BrandInformation brandInformation){
		holder.remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				remove(brandInformation);
			}
		});
	}
	
	
	public void add(int position, BrandInformation brandInformation) {
		mBrandInformationList.add(position, brandInformation);
		notifyItemInserted(position);
	}
	
	
	public void remove(BrandInformation brandInformation) {
		int position = mBrandInformationList.indexOf(brandInformation);
		mBrandInformationList.remove(position);
		notifyItemRemoved(position);
	}
}
