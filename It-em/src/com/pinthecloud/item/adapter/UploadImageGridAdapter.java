package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ImageActivity;
import com.pinthecloud.item.fragment.GalleryFolderFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.UploadFragment;
import com.pinthecloud.item.model.Item;

public class UploadImageGridAdapter extends RecyclerView.Adapter<UploadImageGridAdapter.ViewHolder> {

	private enum TYPE{
		NORMAL,
		BLANK
	}

	private ItApplication mApp;
	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<String> mImageList;


	public UploadImageGridAdapter(ItActivity activity, ItFragment frag, List<String> imageList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mImageList = imageList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView image;
		public ImageButton delete;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_upload_image);
			this.delete = (ImageButton)view.findViewById(R.id.row_upload_image_delete);
		}
	}


	@Override
	public UploadImageGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upload_image_grid, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int viewType = getItemViewType(position);
		if(viewType == TYPE.NORMAL.ordinal()){
			String path = mImageList.get(position);
			setNormalButton(holder, path);
			setNormalImageView(holder, path);
		} else if (viewType == TYPE.BLANK.ordinal()){
			setBlank(holder);
		}
	}


	@Override
	public int getItemCount() {
		int maxGallery = mActivity.getResources().getInteger(R.integer.gallery_max_num);
		return Math.min(mImageList.size()+1, maxGallery);
	}


	@Override
	public int getItemViewType(int position) {
		if(position < mImageList.size()){
			return TYPE.NORMAL.ordinal();
		} else{
			return TYPE.BLANK.ordinal();
		}
	}


	private void setNormalButton(final ViewHolder holder, final String path){
		holder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ImageActivity.class);
				intent.putExtra(Item.INTENT_KEY, path);
				intent.putExtra(ImageActivity.FROM_INTERNET_KEY, false);
				mActivity.startActivity(intent);
			}
		});

		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((UploadFragment)mFrag).deleteImage(path);
			}
		});
	}


	private void setNormalImageView(ViewHolder holder, String path){
		mApp.getPicasso()
		.load(ItConstant.FILE_PREFIX + path)
		.placeholder(R.drawable.upload_image_blank)
		.fit().centerCrop()
		.into(holder.image);

		holder.delete.setVisibility(View.VISIBLE);
	}


	private void setBlank(ViewHolder holder){
		holder.image.setImageResource(R.drawable.upload_image_blank);
		holder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.replaceFragment(new GalleryFolderFragment(), true,
						R.anim.slide_in_up, R.anim.pop_out, R.anim.pop_in, R.anim.slide_out_down);
			}
		});

		holder.delete.setVisibility(View.GONE);
	}


	public void addAll(List<String> pathList) {
		mImageList.addAll(pathList);
		notifyDataSetChanged();
	}


	public void remove(String path) {
		int position = mImageList.indexOf(path);
		mImageList.remove(position);
		notifyDataSetChanged();
	}
}
