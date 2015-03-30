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
import com.pinthecloud.item.fragment.UploadFragment.Brand;

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.ViewHolder> {

	private List<Brand> mBrandList;

	public BrandListAdapter(List<Brand> brandList) {
		this.mBrandList = brandList;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView category;
		public EditText brand;
		public ImageButton remove;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.category = (TextView)view.findViewById(R.id.row_brand_category);
			this.brand = (EditText)view.findViewById(R.id.row_brand_brand);
			this.remove = (ImageButton)view.findViewById(R.id.row_brand_remove);
		}
	}


	@Override
	public BrandListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Brand brand = mBrandList.get(position);
		setComponent(holder, brand);
		setButton(holder, brand);
	}


	@Override
	public int getItemCount() {
		return mBrandList.size();
	}


	private void setComponent(ViewHolder holder, final Brand brand){
		holder.category.setText(brand.getCategory());

		holder.brand.setText(brand.getBrand());
		holder.brand.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				brand.setBrand(s.toString());
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


	private void setButton(ViewHolder holder, final Brand brand){
		holder.remove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				remove(brand);
			}
		});
	}


	public void add(int position, Brand brand) {
		mBrandList.add(position, brand);
		notifyItemInserted(position);
	}


	public void remove(Brand brand) {
		int position = mBrandList.indexOf(brand);
		mBrandList.remove(position);
		notifyItemRemoved(position);
	}
}
