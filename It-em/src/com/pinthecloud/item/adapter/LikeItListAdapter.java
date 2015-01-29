package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.view.CircleImageView;

public class LikeItListAdapter extends RecyclerView.Adapter<LikeItListAdapter.ViewHolder> {

	private ItApplication mApp;
	private ItActivity mActivity;
	private List<LikeIt> mLikeItList;


	public LikeItListAdapter(ItActivity activity, List<LikeIt> likeItList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mLikeItList = likeItList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public CircleImageView profileImage;
		public TextView nickName;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_like_it_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_like_it_list_nick_name);
		}
	}


	@Override
	public LikeItListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_like_it_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		LikeIt likeIt = mLikeItList.get(position);
		setText(holder, likeIt);
		setButton(holder, likeIt);
		setImageView(holder, likeIt);
	}


	@Override
	public int getItemCount() {
		return mLikeItList.size();
	}


	private void setText(ViewHolder holder, final LikeIt likeIt){
		holder.nickName.setText(likeIt.getWhoMade());

	}


	private void setButton(ViewHolder holder, final LikeIt likeIt){
		holder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToItUserPageActivity(likeIt.getWhoMadeId());
			}
		});

		holder.nickName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToItUserPageActivity(likeIt.getWhoMadeId());
			}
		});
	}


	private void setImageView(ViewHolder holder, final LikeIt likeIt){
		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(likeIt.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_s_default_img)
		.fit()
		.into(holder.profileImage);
	}


	public void addAll(List<LikeIt> likeItList) {
		mLikeItList.addAll(likeItList);
		notifyDataSetChanged();
	}


	private void goToItUserPageActivity(String itUserId){
		Intent intent = new Intent(mActivity, ItUserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, itUserId);
		mActivity.startActivity(intent);
	}
}
