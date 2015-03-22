package com.pinthecloud.item.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.interfaces.GalleryCallback;
import com.pinthecloud.item.model.Gallery;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

	private ItApplication mApp;
	private List<Gallery> mGalleryList;

	private GalleryCallback mGalleryCallback;

	public void setGalleryCallback(GalleryCallback galleryCallback) {
		this.mGalleryCallback = galleryCallback;
	}


	public GalleryAdapter(List<Gallery> galleryList) {
		this.mApp = ItApplication.getInstance();
		this.mGalleryList = galleryList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView image;
		public ImageView imageSelected;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_gallery_grid_image);
			this.imageSelected = (ImageView)view.findViewById(R.id.row_gallery_grid_image_selected); 
		}
	}


	@Override
	public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_grid, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Gallery gallery = mGalleryList.get(position);
		setComponent(holder, gallery);
		setImage(holder, gallery);
	}


	@Override
	public int getItemCount() {
		return mGalleryList.size();
	}

	
	private void setComponent(final ViewHolder holder, final Gallery gallery){
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int maxGallery = mApp.getResources().getInteger(R.integer.max_gallery_num);
				if(getSelected().size() < maxGallery || gallery.isSeleted()){
					gallery.setSeleted(!gallery.isSeleted());
					holder.imageSelected.setSelected(gallery.isSeleted());
					mGalleryCallback.clickGallery(gallery);
				}
			}
		});
	}


	private void setImage(ViewHolder holder, Gallery gallery){
		mApp.getPicasso()
		.load(ItConstant.FILE_PREFIX + gallery.getPath())
		.placeholder(R.drawable.feed_loading_default_img)
		.fit().centerCrop()
		.into(holder.image);

		holder.imageSelected.setSelected(gallery.isSeleted());
	}
	
	
	public void addAll(List<Gallery> galleryList) {
		mGalleryList.addAll(galleryList);
		notifyDataSetChanged();
	}


	public List<Gallery> getSelected() {
		List<Gallery> galleryList = new ArrayList<Gallery>();
		for (int i = 0; i < mGalleryList.size(); i++) {
			if (mGalleryList.get(i).isSeleted()) {
				galleryList.add(mGalleryList.get(i));
			}
		}
		return galleryList;
	}
}
