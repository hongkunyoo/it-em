package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
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
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.SpanUtil;

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
		public TextView message;
		public ImageView receiveImage;
		public TextView time;
		public ImageView itemImage;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (ImageView)view.findViewById(R.id.row_noti_profile_image);
			this.message = (TextView)view.findViewById(R.id.row_noti_message);
			this.receiveImage = (ImageView)view.findViewById(R.id.row_noti_receive_image);
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


	private void setComponent(ViewHolder holder, ItNotification noti){
		holder.time.setText(noti.getCreateDateTime().getElapsedTimeString(mApp));
		holder.message.setText(getSpannedMessage(noti));
		holder.message.setMovementMethod(LinkMovementMethod.getInstance());
	}


	private SpannableStringBuilder getSpannedMessage(final ItNotification noti){
		String message = noti.makeMessage();
		int whoMadeStart = message.indexOf(noti.getWhoMade());
		int whoMadeEnd = whoMadeStart+noti.getWhoMade().length();
		int refWhoMadeStart = message.indexOf(noti.getRefWhoMade(), whoMadeStart+1);
		int refWhoMadeEnd = refWhoMadeStart+noti.getRefWhoMade().length();
		
		SpannableStringBuilder spannedMessage = new SpannableStringBuilder(noti.makeMessage());
		if(whoMadeStart != -1){
			SpanUtil.setNickNameSpan(mActivity, spannedMessage, whoMadeStart, whoMadeEnd, noti.getWhoMadeId());
		}
		if(refWhoMadeStart != -1){
			SpanUtil.setNickNameSpan(mActivity, spannedMessage, refWhoMadeStart, refWhoMadeEnd, noti.getRefWhoMadeId());
		}
		return spannedMessage;
	}
	
	
	private void setButton(ViewHolder holder, final ItNotification noti){
		holder.profileImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, UserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, noti.getWhoMadeId());
				mActivity.startActivity(intent);
			}
		});
		
		holder.itemImage.setOnClickListener(new OnClickListener() {

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
			holder.receiveImage.setVisibility(View.VISIBLE);
			holder.receiveImage.setImageResource(R.drawable.noti_like_ic);

			mApp.getPicasso()
			.load(BlobStorageHelper.getUserProfileImgUrl(noti.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
			.placeholder(R.drawable.profile_default_img)
			.fit()
			.into(holder.profileImage);
		} else if(noti.getType().equals(ItNotification.TYPE.Reply.toString())){
			holder.receiveImage.setVisibility(View.VISIBLE);
			holder.receiveImage.setImageResource(R.drawable.noti_comment_ic);

			mApp.getPicasso()
			.load(BlobStorageHelper.getUserProfileImgUrl(noti.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
			.placeholder(R.drawable.profile_default_img)
			.fit()
			.into(holder.profileImage);
		} else if(noti.getType().equals(ItNotification.TYPE.ProductTag.toString())){
			holder.receiveImage.setVisibility(View.GONE);
			holder.profileImage.setImageResource(R.drawable.noti_label_img);
		}

		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(noti.getRefId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.feed_loading_default_img)
		.fit()
		.into(holder.itemImage);
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
