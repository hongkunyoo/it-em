package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.activity.ProductTagActivity;
import com.pinthecloud.item.activity.ReplyActivity;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.SquareImageView;
import com.squareup.picasso.Picasso;

public class HomeItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		FOOTER
	}

	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<Item> mItemList;
	private boolean hasFooter = false;

	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}


	public HomeItemListAdapter(ItActivity activity, ItFragment frag, List<Item> itemList) {
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public SquareImageView image;
		public TextView content;
		public ImageButton itButton;
		public TextView itNumber;
		public Button reply;
		public Button productTag;

		public RelativeLayout profileLayout;
		public CircleImageView profileImage;
		public TextView nickName;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;

			this.image = (SquareImageView)view.findViewById(R.id.row_home_item_list_image);
			this.content = (TextView)view.findViewById(R.id.row_home_item_list_content);
			this.itButton = (ImageButton)view.findViewById(R.id.row_home_item_list_it_button);
			this.itNumber = (TextView)view.findViewById(R.id.row_home_item_list_it_number);
			this.reply = (Button)view.findViewById(R.id.row_home_item_list_reply);
			this.productTag = (Button)view.findViewById(R.id.row_home_item_list_product_tag);

			this.profileLayout = (RelativeLayout)view.findViewById(R.id.row_home_item_list_profile_layout);
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
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalText(normalViewHolder, item);
			setNormalButton(normalViewHolder, item);
			setNormalImageView(normalViewHolder, item, position);
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
		holder.nickName.setText(item.getWhoMade());
	}


	private void setNormalButton(final NormalViewHolder holder, final Item item){
		holder.profileLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItUserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, item.getWhoMadeId());
				mActivity.startActivity(intent);
			}
		});

		holder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});

		holder.itButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int likeItNum = (Integer.parseInt(holder.itNumber.getText().toString()) + 1);
				holder.itNumber.setText(String.valueOf(likeItNum));

				LikeIt likeIt = new LikeIt(item.getWhoMade(), item.getWhoMadeId(), item.getId());
				AimHelper mAimHelper = ItApplication.getInstance().getAimHelper();
				mAimHelper.add(mFrag, likeIt, new ItEntityCallback<LikeIt>() {

					@Override
					public void onCompleted(LikeIt entity) {
					}
				});
			}
		});

		holder.reply.setText(""+item.getReplyCount());
		holder.reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ReplyActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});
		
		holder.productTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ProductTagActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});
	}


	private void setNormalImageView(final NormalViewHolder holder, Item item, int position) {
		Picasso.with(holder.image.getContext())
		.load(BlobStorageHelper.getItemImgUrl(item.getId()))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(holder.image);

		Picasso.with(holder.profileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(item.getWhoMadeId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(holder.profileImage);
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}
}
