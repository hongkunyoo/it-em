package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightImageView;

public class HomeItemGridAdapter extends RecyclerView.Adapter<HomeItemGridAdapter.ViewHolder> {

	private final float MAX_HEIGHT_RATIO = 2.3f;

	private ItApplication mApp;
	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<Item> mItemList;
	private ItUser mMyItUser;

	private String mIt;
	private String mThisIsIt;
	private boolean isDoingIt = false;


	public HomeItemGridAdapter(ItActivity activity, ItFragment frag, List<Item> itemList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;
		this.mMyItUser = mApp.getObjectPrefHelper().get(ItUser.class);
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public CircleImageView profileImage;
		public TextView nickName;
		public ImageButton more;

		public DynamicHeightImageView itemImage;
		public ImageView unfold;
		public TextView content;
		public TextView itNumber;
		public TextView replyNumber;
		public Button itButton;
		public Button productTag;

		public ViewHolder(View view) {
			super(view);
			this.view = view;

			this.profileImage = (CircleImageView)view.findViewById(R.id.row_home_item_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_home_item_nick_name);
			this.more = (ImageButton)view.findViewById(R.id.row_home_item_more);

			this.itemImage = (DynamicHeightImageView)view.findViewById(R.id.row_home_item_item_image);
			this.unfold = (ImageView)view.findViewById(R.id.row_home_item_unfold);
			this.content = (TextView)view.findViewById(R.id.row_home_item_content);
			this.itNumber = (TextView)view.findViewById(R.id.row_home_item_it_number);
			this.replyNumber = (TextView)view.findViewById(R.id.row_home_item_reply_number);
			this.productTag = (Button)view.findViewById(R.id.row_home_item_product_tag);
			this.itButton = (Button)view.findViewById(R.id.row_home_item_it_button);
		}
	}


	@Override
	public HomeItemGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item_grid, parent, false);
		return new ViewHolder(view);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		Item item = mItemList.get(position);
		setComponent(holder, item);
		setButton(holder, item);
		setImageView(holder, item);
	}


	@Override
	public int getItemCount() {
		return mItemList.size();
	}


	private void setComponent(ViewHolder holder, Item item){
		mIt = mActivity.getResources().getString(R.string.it);
		mThisIsIt = mActivity.getResources().getString(R.string.this_is_it);
		
		holder.nickName.setText(item.getWhoMade());
		holder.content.setText(item.getContent());

		setItNumber(holder, item.getLikeItCount());

		if(item.getReplyCount() <= 0){
			holder.replyNumber.setVisibility(View.GONE);
		} else {
			holder.replyNumber.setVisibility(View.VISIBLE);
		}
		holder.replyNumber.setText(""+item.getReplyCount());

		holder.productTag.setEnabled(item.isHasProductTag());
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

		holder.more.setVisibility(item.checkMine() || mApp.isAdmin() ? View.VISIBLE : View.GONE);
		holder.more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = mActivity.getResources().getStringArray(R.array.home_more_array);
				DialogCallback[] callbacks = getDialogCallbacks(itemList, item);

				ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
				listDialog.setCallbacks(callbacks);
				listDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		holder.itemImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEventGA(
						mFrag.getClass().getSimpleName(), GAHelper.VIEW_ITEM, GAHelper.HOME);

				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});

		holder.content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEventGA(
						mFrag.getClass().getSimpleName(), GAHelper.VIEW_ITEM, GAHelper.HOME);

				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});

		holder.itButton.setActivated(item.getPrevLikeId() != null);
		holder.itButton.setText(item.getPrevLikeId() != null ? mThisIsIt : mIt);
		holder.itButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final boolean isDoIt = !holder.itButton.isActivated();
				final int currentItNum = Integer.parseInt(holder.itNumber.getText().toString());
				setItButton(holder, currentItNum, isDoIt);

				if(!isDoingIt){
					isDoingIt = true;

					if(isDoIt) {
						mApp.getGaHelper().sendEventGA(mFrag.getClass().getSimpleName(), GAHelper.IT, GAHelper.HOME);

						// Do like it
						LikeIt it = new LikeIt(mMyItUser.getNickName(), mMyItUser.getId(), item.getId());
						ItNotification noti = new ItNotification(mMyItUser.getNickName(), mMyItUser.getId(), item.getId(),
								item.getWhoMade(), item.getWhoMadeId(), "", ItNotification.TYPE.LikeIt,
								item.getImageWidth(), item.getImageHeight());
						mApp.getAimHelper().addUnique(it, noti, new EntityCallback<LikeIt>() {

							@Override
							public void onCompleted(LikeIt entity) {
								doIt(holder, item, entity.getId(), currentItNum, isDoIt);
							}
						});
					} else {
						mApp.getGaHelper().sendEventGA(mFrag.getClass().getSimpleName(), GAHelper.IT_CANCEL, GAHelper.HOME);

						// Cancel like it
						LikeIt it = new LikeIt(item.getPrevLikeId());
						mApp.getAimHelper().del(it, new EntityCallback<Boolean>() {

							@Override
							public void onCompleted(Boolean entity) {
								doIt(holder, item, null, currentItNum, isDoIt);
							}
						});
					}
				}
			}
		});

		holder.productTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEventGA(
						mFrag.getClass().getSimpleName(), GAHelper.ITEM_TAG_INFORMATION, GAHelper.ITEM);

				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(item, null);
				productTagDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setImageView(final ViewHolder holder, Item item) {
		double heightRatio = Math.min((double)item.getImageHeight()/item.getImageWidth(), MAX_HEIGHT_RATIO);
		holder.itemImage.setHeightRatio(heightRatio);
		if(heightRatio < MAX_HEIGHT_RATIO){
			holder.unfold.setVisibility(View.GONE);
		} else {
			holder.unfold.setVisibility(View.VISIBLE);
		}

		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImgUrl(item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX))
		.placeholder(R.drawable.feed_loading_default_img)
		.into(holder.itemImage);

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(item.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_s_default_img)
		.fit()
		.into(holder.profileImage);
	}


	private void doIt(ViewHolder holder, Item item, String likeId, int currentItNum, boolean isDoIt){
		isDoingIt = false;
		item.setPrevLikeId(likeId);
		setItButton(holder, currentItNum, isDoIt);

		if(isDoIt){
			// Do it
			item.setLikeItCount(currentItNum+1);
		} else {
			// Cancel it
			item.setLikeItCount(currentItNum-1);
		}
	}


	private void setItButton(ViewHolder holder, int currentItNum, boolean isDoIt){
		if(isDoIt) {
			// Do it
			setItNumber(holder, currentItNum+1);
			holder.itButton.setActivated(true);
			holder.itButton.setText(mThisIsIt);
		} else {
			// Cancel it
			setItNumber(holder, currentItNum-1);
			holder.itButton.setActivated(false);
			holder.itButton.setText(mIt);
		}
	}


	private void setItNumber(ViewHolder holder, int itNumber){
		if(itNumber <= 0){
			holder.itNumber.setVisibility(View.GONE);
		} else {
			holder.itNumber.setVisibility(View.VISIBLE);
		}
		holder.itNumber.setText(""+itNumber);
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
