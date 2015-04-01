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
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.view.CircleImageView;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.ViewHolder> {

	private ItApplication mApp;
	private ItActivity mActivity;
	private List<LikeIt> mLikeList;


	public LikeListAdapter(ItActivity activity, List<LikeIt> likeList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mLikeList = likeList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public CircleImageView profileImage;
		public TextView nickName;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_like_it_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_like_it_nick_name);
		}
	}


	@Override
	public LikeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_like_it_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		LikeIt like = mLikeList.get(position);
		setText(holder, like);
		setButton(holder, like);
		setImageView(holder, like);
	}


	@Override
	public int getItemCount() {
		return mLikeList.size();
	}


	private void setText(ViewHolder holder, final LikeIt like){
		holder.nickName.setText(like.getWhoMade());
	}


	private void setButton(ViewHolder holder, final LikeIt like){
		holder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoUserPageActivity(like.getWhoMadeId());
			}
		});

		holder.nickName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoUserPageActivity(like.getWhoMadeId());
			}
		});
	}


	private void setImageView(ViewHolder holder, final LikeIt like){
		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(like.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(holder.profileImage);
	}


	public void addAll(List<LikeIt> likeList) {
		mLikeList.addAll(likeList);
		notifyDataSetChanged();
	}


	private void gotoUserPageActivity(String userId){
		Intent intent = new Intent(mActivity, UserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, userId);
		mActivity.startActivity(intent);
	}
}
