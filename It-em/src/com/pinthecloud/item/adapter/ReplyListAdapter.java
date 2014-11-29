package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ViewHolder> {

	private Context mContext;
	private List<Reply> mReplyList;


	public ReplyListAdapter(Context context, List<Reply> replyList) {
		this.mContext = context;
		this.mReplyList = replyList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public CircleImageView profileImage;
		public TextView nickName;
		public TextView content;
		public ProgressBar progressBar;
		public TextView time;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_reply_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_reply_list_nick_name);
			this.content = (TextView)view.findViewById(R.id.row_reply_list_content);
			this.progressBar = (ProgressBar)view.findViewById(R.id.row_reply_list_progress_bar);
			this.time = (TextView)view.findViewById(R.id.row_reply_list_time);
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
		setImageView(holder, reply);
	}


	@Override
	public int getItemCount() {
		return mReplyList.size();
	}


	private void setText(ViewHolder holder, Reply reply){
		holder.nickName.setText(reply.getWhoMade());
		holder.content.setText(reply.getContent());
	}


	private void setImageView(ViewHolder holder, Reply reply) {
		Picasso.with(mContext)
		.load(BlobStorageHelper.getItemImgUrl(reply.getWhoMadeId()))
		.placeholder(R.drawable.ic_launcher)
		.error(R.drawable.ic_launcher)
		.fit()
		.into(holder.profileImage);
	}


	public void add(Reply reply, int position) {
		mReplyList.add(position, reply);
		notifyItemInserted(position);
	}


	public void remove(Reply reply) {
		int position = mReplyList.indexOf(reply);
		mReplyList.remove(position);
		notifyItemRemoved(position);
	}
}
