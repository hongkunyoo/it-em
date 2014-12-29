package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ReplyDialog;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class ReplyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		PREVIOUS,
		NORMAL
	}

	private Context mContext;
	private ItFragment mFrag;
	private List<Reply> mReplyList;
	private ItUser mMyItUser;
	private Item mItem;
	private boolean mHasPrevious = false;

	public void setHasPrevious(boolean hasPrevious) {
		this.mHasPrevious = hasPrevious;
	}


	public ReplyListAdapter(Context context, ItFragment frag, ItUser myItUser, Item item, List<Reply> replyList) {
		this.mContext = context;
		this.mFrag = frag;
		this.mMyItUser = myItUser;
		this.mItem = item;
		this.mReplyList = replyList;
	}


	public static class PreviousViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public PreviousViewHolder(View view) {
			super(view);
			this.view = view;
		}
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public CircleImageView profileImage;
		public TextView nickName;
		public TextView time;
		public TextView content;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_reply_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_reply_list_nick_name);
			this.time = (TextView)view.findViewById(R.id.row_reply_list_time);
			this.content = (TextView)view.findViewById(R.id.row_reply_list_content);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.PREVIOUS.ordinal()){
			view = inflater.inflate(R.layout.row_reply_list_previous, parent, false);
			viewHolder = new PreviousViewHolder(view);
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_reply_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.PREVIOUS.ordinal()){
			PreviousViewHolder preiousViewHolder = (PreviousViewHolder)holder;
			setPreviousButton(preiousViewHolder);
		} else if (viewType == VIEW_TYPE.NORMAL.ordinal()){
			if(mHasPrevious) --position;
			Reply reply = mReplyList.get(position);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNoramlText(normalViewHolder, reply);
			setNormalButton(normalViewHolder, reply);
			setNormalImageView(normalViewHolder, reply);
		}
	}


	@Override
	public int getItemCount() {
		if(mHasPrevious){
			return mReplyList.size()+1;
		} else{
			return mReplyList.size();
		}
	}


	@Override
	public int getItemViewType(int position) {
		if(mHasPrevious){
			if (position == 0) {
				return VIEW_TYPE.PREVIOUS.ordinal();
			} else {
				return VIEW_TYPE.NORMAL.ordinal();
			}
		} else{
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setPreviousButton(PreviousViewHolder holder){
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReplyDialog replyDialog = new ReplyDialog(mFrag, mItem);
				replyDialog.show(mFrag.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setNoramlText(NormalViewHolder holder, final Reply reply){
		holder.nickName.setText(reply.getWhoMade());
		holder.nickName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToItUserPageActivity(reply.getWhoMadeId());
			}
		});

		holder.content.setText(reply.getContent());
		if(reply.getRawCreateDateTime() != null){
			holder.time.setText(reply.getCreateDateTime().getElapsedDateTime());
		} else {
			holder.time.setText("");
		}
	}


	private void setNormalButton(NormalViewHolder holder, final Reply reply){
		if(reply.getWhoMadeId().equals(mMyItUser.getId())){
			holder.view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					String[] itemList = mContext.getResources().getStringArray(R.array.reply_long_click_string_array);
					DialogCallback[] callbacks = getDialogCallbacks(itemList, reply);
					ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
					listDialog.show(mFrag.getFragmentManager(), ItDialogFragment.INTENT_KEY);
					return false;
				}
			});
		} else {
			holder.view.setOnLongClickListener(null);
		}
	}


	private void setNormalImageView(NormalViewHolder holder, final Reply reply) {
		Picasso.with(holder.profileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(reply.getWhoMadeId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(holder.profileImage);

		holder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToItUserPageActivity(reply.getWhoMadeId());
			}
		});
	}


	public void add(int position, Reply reply) {
		mReplyList.add(position, reply);
		notifyItemInserted(position);
	}


	public void addAll(List<Reply> replyList) {
		mReplyList.addAll(replyList);
		notifyDataSetChanged();
	}


	public void remove(Reply reply) {
		int position = mReplyList.indexOf(reply);
		mReplyList.remove(position);
		notifyItemRemoved(position);
	}


	public void replace(int position, Reply reply){
		mReplyList.set(position, reply);
		notifyItemChanged(position);
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList, final Reply reply){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];
		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				deleteReply(reply);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};
		return callbacks;
	}


	private void deleteReply(final Reply reply){
		AimHelper aimHelper = ItApplication.getInstance().getAimHelper();
		aimHelper.del(mFrag, reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				remove(reply);
			}
		});
	}


	private void goToItUserPageActivity(String itUserId){
		Intent intent = new Intent(mContext, ItUserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, itUserId);
		mContext.startActivity(intent);
	}
}
