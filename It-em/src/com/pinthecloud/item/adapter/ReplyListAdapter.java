package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.ItDialogCallback;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ViewHolder> {

	private Context mContext;
	private ItFragment mFrag;
	private List<Reply> mReplyList;


	public ReplyListAdapter(Context context, ItFragment frag, List<Reply> replyList) {
		this.mContext = context;
		this.mFrag = frag;
		this.mReplyList = replyList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public CircleImageView profileImage;
		public TextView nickName;
		public TextView time;
		public TextView content;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_reply_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_reply_list_nick_name);
			this.time = (TextView)view.findViewById(R.id.row_reply_list_time);
			this.content = (TextView)view.findViewById(R.id.row_reply_list_content);
		}
	}


	@Override
	public ReplyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reply_list, parent, false);
		return new ViewHolder(itemView);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Reply reply = mReplyList.get(position);
		setText(holder, reply);
		setButton(holder, reply);
		setImageView(holder, reply);
	}


	@Override
	public int getItemCount() {
		return mReplyList.size();
	}


	private void setText(ViewHolder holder, Reply reply){
		holder.nickName.setText(reply.getWhoMade());
		holder.content.setText(reply.getContent());
		if(reply.getRawCreateDateTime() != null){
			holder.time.setText(reply.getCreateDateTime().getElapsedDateTimeString());
		}
	}


	private void setButton(ViewHolder holder, final Reply reply){
		holder.view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String[] itemList = mContext.getResources().getStringArray(R.array.reply_long_click_string_array);
				ItDialogCallback[] callbacks = getDialogCallbacks(itemList, reply);
				ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
				listDialog.show(mFrag.getFragmentManager(), ItDialogFragment.INTENT_KEY);
				return false;
			}
		});
	}


	private void setImageView(ViewHolder holder, Reply reply) {
		Picasso.with(mContext)
		.load(BlobStorageHelper.getItemImgUrl(reply.getWhoMadeId()))
		.placeholder(R.drawable.ic_launcher)
		.error(R.drawable.ic_launcher)
		.fit()
		.into(holder.profileImage);
	}


	public void add(int position, Reply reply) {
		mReplyList.add(position, reply);
		notifyItemInserted(position);
	}


	public void addAll(int position, List<Reply> replyList) {
		mReplyList.addAll(position, replyList);
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


	private ItDialogCallback[] getDialogCallbacks(String[] itemList, final Reply reply){
		ItDialogCallback[] callbacks = new ItDialogCallback[itemList.length];
		callbacks[0] = new ItDialogCallback() {

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
		aimHelper.del(mFrag, reply, new ItEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				remove(reply);
			}
		});
	}
}
