package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.model.NotiRecord;

public class NotiListAdapter extends RecyclerView.Adapter<NotiListAdapter.ViewHolder> {

	private List<NotiRecord> mNotiList;

	public NotiListAdapter(List<NotiRecord> notiList) {
		this.mNotiList = notiList;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
		}
	}


	@Override
	public NotiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_noti_list, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		NotiRecord notiRecord = mNotiList.get(position);
	}


	@Override
	public int getItemCount() {
		return mNotiList.size();
	}
}
