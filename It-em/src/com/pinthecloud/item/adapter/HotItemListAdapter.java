package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.activity.OtherPageActivity;
import com.pinthecloud.item.activity.ReplyActivity;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.SquareImageView;

public class HotItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		FOOTER
	}

	private Context mContext;
	private ItFragment mFrag;
	private List<Item> mItemList;
	private boolean hasFooter = false;

	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}


	public HotItemListAdapter(Context context, ItFragment frag, List<Item> itemList) {
		this.mContext = context;
		this.mFrag = frag;
		this.mItemList = itemList;
		setHasStableIds(true);
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public LinearLayout profileLayout;
		public TextView rank;
		public CircleImageView profileImage;
		public TextView nickName;

		public LinearLayout itemLayout;
		public SquareImageView image;
		public TextView content;
		public Button itButton;
		public TextView itNumber;
		public Button reply;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;

			this.profileLayout = (LinearLayout)view.findViewById(R.id.row_home_item_list_profile_layout);
			this.rank = (TextView)view.findViewById(R.id.row_hot_item_list_rank);
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_hot_item_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_hot_item_list_nick_name);

			this.itemLayout = (LinearLayout)view.findViewById(R.id.row_home_item_list_item_layout);
			this.image = (SquareImageView)view.findViewById(R.id.row_hot_item_list_image);
			this.content = (TextView)view.findViewById(R.id.row_hot_item_list_content);
			this.itButton = (Button)view.findViewById(R.id.row_hot_item_list_it_button);
			this.itNumber = (TextView)view.findViewById(R.id.row_hot_item_list_it_number);
			this.reply = (Button)view.findViewById(R.id.row_hot_item_list_reply);
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
			view = inflater.inflate(R.layout.row_hot_item_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.FOOTER.ordinal()){
			view = inflater.inflate(R.layout.row_home_item_list_footer, parent, false);
			viewHolder = new FooterViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(position);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalText(normalViewHolder, item);
			setNormalButton(normalViewHolder, item);
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


	private void setNormalText(NormalViewHolder holder, Item item){
		holder.content.setText(item.getContent());
		holder.itNumber.setText(""+item.getLikeItCount());
	}


	private void setNormalButton(NormalViewHolder holder, Item item){
		holder.profileLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, OtherPageActivity.class);
				mContext.startActivity(intent);
			}
		});

		holder.itemLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ItemActivity.class);
				mContext.startActivity(intent);
			}
		});

		holder.itButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		holder.reply.setText(""+item.getReplyCount());
		holder.reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ReplyActivity.class);
				mContext.startActivity(intent);
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
