package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.SquareImageView;

public class HomeItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		FOOTER
	}

	private ItFragment mFrag;
	private List<Item> mItemList;
	private boolean hasFooter = false;

	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}


	public HomeItemListAdapter(ItFragment frag, List<Item> itemList) {
		this.mFrag = frag;
		this.mItemList = itemList;
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public SquareImageView image;
		public TextView content;
		public Button itButton;
		public TextView itNumber;
		public Button reply;
		public CircleImageView profileImage;
		public TextView nickName;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.image = (SquareImageView)view.findViewById(R.id.row_home_item_list_image);
			this.content = (TextView)view.findViewById(R.id.row_home_item_list_content);
			this.itButton = (Button)view.findViewById(R.id.row_home_item_list_it_button);
			this.itNumber = (TextView)view.findViewById(R.id.row_home_item_list_it_number);
			this.reply = (Button)view.findViewById(R.id.row_home_item_list_reply);
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_home_item_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_home_item_list_nick_name);
		}
	}


	public static class FooterViewHolder extends RecyclerView.ViewHolder {
		public View mView;
		public ProgressBar mProgressBar;

		public FooterViewHolder(View view) {
			super(view);
			this.mView = view;
			this.mProgressBar = (ProgressBar)view.findViewById(R.id.row_home_item_list_footer_progress_bar);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_home_item_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.FOOTER.ordinal()){
			view = inflater.inflate(R.layout.row_home_item_list_footer, parent, false);
			viewHolder = new FooterViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(position);
			NormalViewHolder viewHolder = (NormalViewHolder)holder;
			setNormalComponent(viewHolder, item);
		}
	}


	@Override
	public int getItemCount() {
		if(hasFooter){
			return mItemList.size()+1;
		} else{
			return mItemList.size();
		}
	}


	@Override
	public int getItemViewType(int position) {
		if(hasFooter){
			if (position < getItemCount()-1) {
				return VIEW_TYPE.NORMAL.ordinal();
			} else{
				return VIEW_TYPE.FOOTER.ordinal();
			}
		} else{
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setNormalComponent(NormalViewHolder holder, Item item){
		setNormalText(holder, item);
		setNormalButton(holder, item);
	}


	private void setNormalText(NormalViewHolder holder, Item item){
		holder.content.setText(item.getContent());
		holder.itNumber.setText(""+item.getLikeItCount());
	}


	private void setNormalButton(NormalViewHolder holder, Item item){
		holder.itButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		holder.reply.setText(""+item.getReplyCount());
		holder.reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
	}


	public void add(Item item, int position) {
		mItemList.add(position, item);
		notifyItemInserted(position);
	}


	public void remove(Item item) {
		int position = mItemList.indexOf(item);
		mItemList.remove(position);
		notifyItemRemoved(position);
	}
}
