package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.fragment.GalleryFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.GalleryFolder;

public class GalleryFolderAdapter extends RecyclerView.Adapter<GalleryFolderAdapter.ViewHolder> {

	private ItApplication mApp;
	private ItActivity mActivity;
	private List<GalleryFolder> mFolderList;


	public GalleryFolderAdapter(ItActivity activity, List<GalleryFolder> folderList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFolderList = folderList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView image;
		public TextView name;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_gallery_folder_list_image);
			this.name = (TextView)view.findViewById(R.id.row_gallery_folder_list_name); 
		}
	}


	@Override
	public GalleryFolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_folder_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		GalleryFolder folder = mFolderList.get(position);
		setComponent(holder, folder);
		setImage(holder, folder);
	}


	@Override
	public int getItemCount() {
		return mFolderList.size();
	}


	private void setComponent(ViewHolder holder, final GalleryFolder folder){
		holder.name.setText(folder.getName() + " (" + folder.getGalleryList().size() + ")");
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItFragment fragment = GalleryFragment.newInstance(folder);
				mActivity.replaceFragment(fragment, true, R.anim.slide_in_pop_up, 0, R.anim.pop_in, R.anim.slide_out_pop_down);
			}
		});
	}


	private void setImage(final ViewHolder holder, final GalleryFolder folder){
		mApp.getPicasso()
		.load(ItConstant.FILE_PREFIX + folder.getGalleryList().get(0).getPath())
		.placeholder(R.drawable.feed_loading_default_img)
		.fit().centerCrop()
		.into(holder.image);
	}


	public void addAll(List<GalleryFolder> folderList) {
		mFolderList.addAll(folderList);
		notifyDataSetChanged();
	}
}
