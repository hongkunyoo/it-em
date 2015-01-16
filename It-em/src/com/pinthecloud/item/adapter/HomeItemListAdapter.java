package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ReplyDialog;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightImageView;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.ViewHolder> {

	private ItApplication mApp;
	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<Item> mItemList;


	public HomeItemListAdapter(ItActivity activity, ItFragment frag, List<Item> itemList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public CircleImageView profileImage;
		public TextView nickName;
		public ImageButton more;

		public LinearLayout itemLayout; 
		public DynamicHeightImageView itemImage;
		public TextView content;
		public TextView itNumber;
		public TextView replyNumber;
		public ImageButton itButton;

		public ViewHolder(View view) {
			super(view);
			this.view = view;

			this.profileImage = (CircleImageView)view.findViewById(R.id.row_home_item_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_home_item_list_nick_name);
			this.more = (ImageButton)view.findViewById(R.id.row_home_item_list_more);

			this.itemLayout = (LinearLayout)view.findViewById(R.id.row_home_item_list_item_layout);
			this.itemImage = (DynamicHeightImageView)view.findViewById(R.id.row_home_item_list_item_image);
			this.content = (TextView)view.findViewById(R.id.row_home_item_list_content);
			this.itNumber = (TextView)view.findViewById(R.id.row_home_item_list_it_number);
			this.replyNumber = (TextView)view.findViewById(R.id.row_home_item_list_reply_number);
			this.itButton = (ImageButton)view.findViewById(R.id.row_home_item_list_it_button);
		}
	}


	@Override
	public HomeItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item_grid, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Item item = mItemList.get(position);
		setComponent(holder, item);
		setButton(holder, item);
		setImageView(holder, item, position);
	}


	@Override
	public int getItemCount() {
		return mItemList.size();
	}


	private void setComponent(ViewHolder holder, Item item){
		holder.nickName.setText(item.getWhoMade());
		holder.content.setText(item.getContent());

		if(item.getLikeItCount() <= 0){
			holder.itNumber.setText("");	
		} else {
			holder.itNumber.setText(""+item.getLikeItCount());
		}

		if(item.getReplyCount() <= 0){
			holder.replyNumber.setText("");
		} else {
			holder.replyNumber.setText(""+item.getReplyCount());
		}
	}


	private void setButton(final ViewHolder holder, final Item item){
		holder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItUserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, item.getWhoMadeId());
				mActivity.startActivity(intent);
			}
		});

		holder.nickName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItUserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, item.getWhoMadeId());
				mActivity.startActivity(intent);
			}
		});

		if(item.checkIsMine()){
			holder.more.setVisibility(View.VISIBLE);
			holder.more.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String[] itemList = mActivity.getResources().getStringArray(R.array.home_more_string_array);
					DialogCallback[] callbacks = getDialogCallbacks(itemList, item);

					ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
					listDialog.setCallbacks(callbacks);
					listDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
				}
			});
		} else {
			holder.more.setVisibility(View.GONE);
			holder.more.setOnClickListener(null);
		}

		holder.itemLayout.setOnClickListener(new OnClickListener() {

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
				int likeItNum = Integer.parseInt(holder.itNumber.getText().toString());
				if(holder.itButton.isActivated()) {
					// Cancel like it
					likeItNum--;
				} else {
					// Do like it
					likeItNum++;

					ItUser myItUser = mApp.getObjectPrefHelper().get(ItUser.class);
					LikeIt likeIt = new LikeIt(myItUser.getNickName(), myItUser.getId(), item.getId());
					mApp.getAimHelper().add(likeIt, new EntityCallback<LikeIt>() {

						@Override
						public void onCompleted(LikeIt entity) {
							item.setLikeItCount(Integer.parseInt(holder.itNumber.getText().toString()));
						}
					});
				}
				holder.itNumber.setText(""+likeItNum);
				holder.itButton.setActivated(!holder.itButton.isActivated());
			}
		});

		holder.replyNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment replyDialog = ReplyDialog.newInstance(item);
				replyDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setImageView(final ViewHolder holder, Item item, int position) {
		double heightRatio = Math.min((double)item.getImageHeight()/item.getImageWidth(), 2.5);
		holder.itemImage.setHeightRatio(heightRatio);

		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX))
		.fit().centerCrop()
		.into(holder.itemImage);

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(item.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.fit()
		.into(holder.profileImage);
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList, final Item item){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];
		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				deleteItem(item);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};
		return callbacks;
	}


	private void deleteItem(final Item item){
		mApp.getAimHelper().delItem(mFrag, item, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				remove(item);
			}
		});
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}


	public void add(int position, Item item) {
		mItemList.add(position, item);
		notifyItemInserted(position);
	}


	public void remove(Item item) {
		int position = mItemList.indexOf(item);
		mItemList.remove(position);
		notifyItemRemoved(position);
	}
}
