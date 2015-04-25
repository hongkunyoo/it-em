package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.GAHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.ItLike;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.ViewUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.pinthecloud.item.view.RoundedTopCornerTransformation;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.ViewHolder> {

	private ItApplication mApp;
	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<Item> mItemList;

	private double MAX_HEIGHT_RATIO;
	private boolean isDoingLike = false;


	public HomeItemListAdapter(ItActivity activity, ItFragment frag, int gridColumnNum, List<Item> itemList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;

		int width = ViewUtil.getDeviceWidth(activity);
		int height = ViewUtil.getDeviceHeight(activity);
		int hiddenHeight = ViewUtil.getActionBarHeight(activity)*3 + ViewUtil.getStatusBarHeight(activity);
		this.MAX_HEIGHT_RATIO = (double)(gridColumnNum*(height-hiddenHeight))/width;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public DynamicHeightImageView itemImage;
		public TextView imageNumber;
		public View unfold;

		public TextView content;
		public TextView likeNumber;
		public TextView replyNumber;
		public ImageButton likeButton;
		public Button productTag;

		public View profileLayout;
		public CircleImageView profileImage;
		public TextView nickName;

		public ViewHolder(View view) {
			super(view);
			this.view = view;

			this.itemImage = (DynamicHeightImageView)view.findViewById(R.id.row_home_item_image);
			this.imageNumber = (TextView)view.findViewById(R.id.row_home_item_image_number);
			this.unfold = view.findViewById(R.id.row_home_item_unfold);

			this.content = (TextView)view.findViewById(R.id.row_home_item_content);
			this.likeNumber = (TextView)view.findViewById(R.id.row_home_item_like_number);
			this.replyNumber = (TextView)view.findViewById(R.id.row_home_item_reply_number);
			this.likeButton = (ImageButton)view.findViewById(R.id.row_home_item_like_button);
			this.productTag = (Button)view.findViewById(R.id.row_home_item_product_tag);

			this.profileLayout = view.findViewById(R.id.row_home_item_profile_layout);
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_home_item_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_home_item_nick_name);
		}
	}


	@Override
	public HomeItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item_list, parent, false);
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
		holder.imageNumber.setText(""+item.getImageNumber());
		holder.imageNumber.setVisibility(item.getImageNumber() > 1 ? View.VISIBLE : View.GONE);

		holder.nickName.setText(item.getWhoMade());
		holder.content.setText(item.getContent());

		setLikeNumber(holder, item.getLikeCount());
		holder.replyNumber.setVisibility(item.getReplyCount() > 0 ? View.VISIBLE : View.GONE);
		holder.replyNumber.setText(""+item.getReplyCount());

		holder.productTag.setActivated(item.isHasProductTag());
	}


	private void setButton(final ViewHolder holder, final Item item){
		holder.itemImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.VIEW_ITEM, GAHelper.HOME);

				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});

		holder.likeButton.setActivated(item.getPrevLikeId() != null);
		holder.likeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final boolean isDoLike = !holder.likeButton.isActivated();
				final int currentLikeNum = Integer.parseInt(holder.likeNumber.getText().toString());
				setLikeButton(holder, currentLikeNum, isDoLike);

				if(isDoingLike){
					return;
				}

				isDoingLike = true;
				if(isDoLike) {
					mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.LIKE, GAHelper.HOME);

					// Do like it
					ItUser user = mApp.getObjectPrefHelper().get(ItUser.class);
					ItLike like = new ItLike(user.getNickName(), user.getId(), item.getId());
					ItNotification noti = new ItNotification(user.getNickName(), user.getId(), item.getId(),
							item.getWhoMade(), item.getWhoMadeId(), "", ItNotification.TYPE.ItLike,
							item.getImageNumber(), item.getMainImageWidth(), item.getMainImageHeight());
					mApp.getAimHelper().addUnique(like, noti, new EntityCallback<ItLike>() {

						@Override
						public void onCompleted(ItLike entity) {
							doLike(holder, item, entity.getId(), currentLikeNum, isDoLike);
						}
					});
				} else {
					mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.LIKE_CANCEL, GAHelper.HOME);

					// Cancel like it
					ItLike like = new ItLike(item.getPrevLikeId());
					mApp.getAimHelper().del(like, new EntityCallback<Boolean>() {

						@Override
						public void onCompleted(Boolean entity) {
							doLike(holder, item, null, currentLikeNum, isDoLike);
						}
					});
				}
			}
		});

		holder.productTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.VIEW_PRODUCT_TAG, GAHelper.HOME);

				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(item);
				productTagDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		holder.profileLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoUserPage(item);
			}
		});
	}


	private void gotoUserPage(Item item){
		mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.VIEW_UPLOADER, GAHelper.HOME);

		Intent intent = new Intent(mActivity, UserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, item.getWhoMadeId());
		mActivity.startActivity(intent);
	}


	private void setImageView(final ViewHolder holder, final Item item) {
		double heightRatio = (double)item.getCoverImageHeight()/item.getCoverImageWidth();
		holder.itemImage.setHeightRatio(Math.min(heightRatio, MAX_HEIGHT_RATIO));
		holder.unfold.setVisibility(heightRatio <= MAX_HEIGHT_RATIO ? View.GONE : View.VISIBLE);

		int radius = mActivity.getResources().getDimensionPixelSize(R.dimen.content_margin);
		if(heightRatio <= MAX_HEIGHT_RATIO){
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImageUrl(item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX))
			.placeholder(R.drawable.feed_loading_default_img)
			.transform(new RoundedTopCornerTransformation(radius, 0))
			.into(holder.itemImage);
		} else {
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImageUrl(item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX))
			.placeholder(R.drawable.feed_loading_default_img)
			.transform(new RoundedTopCornerTransformation(radius, 0))
			.resize(item.getCoverImageWidth(), (int) (item.getCoverImageWidth()*MAX_HEIGHT_RATIO)).centerCrop()
			.into(holder.itemImage);
		}

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileUrl(item.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(holder.profileImage);
	}


	private void doLike(ViewHolder holder, Item item, String likeId, int currentLikeNum, boolean isDoLike){
		isDoingLike = false;
		setLikeButton(holder, currentLikeNum, isDoLike);
		item.setPrevLikeId(likeId);
		item.setLikeCount(isDoLike ? currentLikeNum+1 : currentLikeNum-1);
	}


	private void setLikeButton(ViewHolder holder, int currentLikeNum, boolean isDoLike){
		if(isDoLike) {
			// Do Like
			setLikeNumber(holder, currentLikeNum+1);
			holder.likeButton.setActivated(true);
		} else {
			// Cancel Like
			setLikeNumber(holder, currentLikeNum-1);
			holder.likeButton.setActivated(false);
		}
	}


	private void setLikeNumber(ViewHolder holder, int likeNumber){
		holder.likeNumber.setVisibility(likeNumber > 0 ? View.VISIBLE : View.GONE);
		holder.likeNumber.setText(""+likeNumber);
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}


	public void add(int position, Item item) {
		mItemList.add(position, item);
		notifyItemInserted(position);
	}
}
