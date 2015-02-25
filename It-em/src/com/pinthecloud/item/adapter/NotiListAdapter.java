package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ImageUtil;

public class NotiListAdapter extends RecyclerView.Adapter<NotiListAdapter.ViewHolder> {

	private ItApplication mApp;
	private ItActivity mActivity;
	private List<ItNotification> mNotiList;

	public NotiListAdapter(ItActivity activity, List<ItNotification> notiList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mNotiList = notiList;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView profileImage;
		public ImageView receiveImage;
		public TextView receive;
		public TextView content;
		public TextView time;
		public ImageView itemImage;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (ImageView)view.findViewById(R.id.row_noti_profile_image);
			this.receiveImage = (ImageView)view.findViewById(R.id.row_noti_receive_image);
			this.receive = (TextView)view.findViewById(R.id.row_noti_receive);
			this.content = (TextView)view.findViewById(R.id.row_noti_content);
			this.time = (TextView)view.findViewById(R.id.row_noti_time);
			this.itemImage = (ImageView)view.findViewById(R.id.row_noti_image);
		}
	}


	@Override
	public NotiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_noti_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		ItNotification noti = mNotiList.get(position);
		setComponent(holder, noti);
		setButton(holder, noti);
		setImage(holder, noti);
	}


	@Override
	public int getItemCount() {
		return mNotiList.size();
	}


	private void setComponent(ViewHolder holder, final ItNotification noti){
		holder.time.setText(noti.getCreateDateTime().getElapsedDateTime(mApp));
		holder.receive.setText(noti.notiContent());
		holder.content.setText(noti.getContent());

		if(noti.getContent() != null && !noti.getContent().equals("")){
			holder.content.setVisibility(View.VISIBLE);
		} else {
			holder.content.setVisibility(View.GONE);
		}
	}


	private void setButton(ViewHolder holder, final ItNotification noti){
		holder.view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, noti.makeItem());
				mActivity.startActivity(intent);
			}
		});
	}
	
	
	private void setImage(ViewHolder holder, final ItNotification noti){
		if(noti.getType().equals(ItNotification.TYPE.LikeIt.toString())){
			holder.receiveImage.setImageResource(R.drawable.launcher);
		} else if(noti.getType().equals(ItNotification.TYPE.Reply.toString())){
			holder.receiveImage.setImageResource(R.drawable.launcher);
		} else if(noti.getType().equals(ItNotification.TYPE.ProductTag.toString())){
			holder.receiveImage.setImageResource(R.drawable.launcher);
		}

		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(noti.getRefId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.feed_loading_default_img)
		.into(holder.itemImage);

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(noti.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_l_default_img)
		.fit()
		.into(holder.profileImage);
	}


	public void addAll(List<ItNotification> notiList) {
		mNotiList.addAll(notiList);
		notifyDataSetChanged();
	}
	
	
	public void add(int position, ItNotification noti) {
		mNotiList.add(position, noti);
		notifyItemInserted(position);
	}
}
