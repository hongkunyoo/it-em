package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Reply;

public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ViewHolder> {

	private ItFragment mFrag;
	private List<Reply> mReplyList;


	public ReplyListAdapter(ItFragment frag, List<Reply> replyList) {
		this.mFrag = frag;
		this.mReplyList = replyList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
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
	}


	@Override
	public int getItemCount() {
		return mReplyList.size();
	}
}
