package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.dialog.ReplyDialog;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.GAHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.pinthecloud.item.view.RoundedTopCornerTransformation;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.ViewHolder> {

	private final double MAX_HEIGHT_RATIO = 1.1;

	private ItApplication mApp;
	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<Item> mItemList;

	private boolean isDoingLike = false;


	public HomeItemListAdapter(ItActivity activity, ItFragment frag, List<Item> itemList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public DynamicHeightImageView itemImage;
		public TextView imageNumber;
		public View unfold;
		
		public Button likeButton;
		public View replyLayout;
		public TextView reply;
		public Button productTag;
		public TextView content;

		public CircleImageView profileImage;
		public TextView nickName;

		public ViewHolder(View view) {
			super(view);
			this.view = view;

			this.itemImage = (DynamicHeightImageView)view.findViewById(R.id.row_home_item_image);
			this.imageNumber = (TextView)view.findViewById(R.id.row_home_item_image_number);
			this.unfold = view.findViewById(R.id.row_home_item_unfold);
			
			this.likeButton = (Button)view.findViewById(R.id.row_home_item_like_button);
			this.replyLayout = view.findViewById(R.id.row_home_item_reply_layout);
			this.reply = (TextView)view.findViewById(R.id.row_home_item_reply);
			this.productTag = (Button)view.findViewById(R.id.row_home_item_product_tag);
			this.content = (TextView)view.findViewById(R.id.row_home_item_content);

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
		setProfileButton(holder, item);
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

		holder.replyLayout.setVisibility(item.getReplyCount() > 0 ? View.VISIBLE : View.GONE);
		holder.reply.setText(""+item.getReplyCount());

		holder.productTag.setActivated(item.isHasProductTag());
	}


	private void setButton(final ViewHolder holder, final Item item){
		holder.itemImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoItem(item);
			}
		});

		final String like = mActivity.getResources().getString(R.string.like);
		holder.likeButton.setText(item.getLikeItCount() == 0 ? like : ""+item.getLikeItCount());
		holder.likeButton.setActivated(item.getPrevLikeId() != null);
		holder.likeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String likeNum = holder.likeButton.getText().toString();
				final int currentLikeNum = likeNum.equals(like) ? 0 : Integer.parseInt(likeNum);
				final boolean isDoLike = !holder.likeButton.isActivated();
				setLikeButton(holder, currentLikeNum, isDoLike);

				if(!isDoingLike){
					isDoingLike = true;

					if(isDoLike) {
						mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.LIKE, GAHelper.HOME);

						// Do like it
						ItUser user = mApp.getObjectPrefHelper().get(ItUser.class);
						LikeIt like = new LikeIt(user.getNickName(), user.getId(), item.getId());
						ItNotification noti = new ItNotification(user.getNickName(), user.getId(), item.getId(),
								item.getWhoMade(), item.getWhoMadeId(), "", ItNotification.TYPE.LikeIt,
								item.getImageNumber(), item.getImageWidth(), item.getImageHeight());
						mApp.getAimHelper().addUnique(like, noti, new EntityCallback<LikeIt>() {

							@Override
							public void onCompleted(LikeIt entity) {
								doLike(holder, item, entity.getId(), currentLikeNum, isDoLike);
							}
						});
					} else {
						mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.LIKE_CANCEL, GAHelper.HOME);

						// Cancel like it
						LikeIt like = new LikeIt(item.getPrevLikeId());
						mApp.getAimHelper().del(like, new EntityCallback<Boolean>() {

							@Override
							public void onCompleted(Boolean entity) {
								doLike(holder, item, null, currentLikeNum, isDoLike);
							}
						});
					}
				}
			}
		});

		holder.replyLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment replyDialog = ReplyDialog.newInstance(item);
				replyDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		holder.productTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.VIEW_PRODUCT_TAG, GAHelper.HOME);

				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(item, null);
				productTagDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setProfileButton(ViewHolder holder, final Item item){
		holder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoUserPage(item);
			}
		});

		holder.nickName.setOnClickListener(new OnClickListener() {

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


	private void gotoItem(Item item){
		mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.VIEW_ITEM, GAHelper.HOME);

		Intent intent = new Intent(mActivity, ItemActivity.class);
		intent.putExtra(Item.INTENT_KEY, item);
		mActivity.startActivity(intent);
	}


	private void setImageView(final ViewHolder holder, final Item item) {
		double heightRatio = Math.min((double)item.getImageHeight()/item.getImageWidth(), MAX_HEIGHT_RATIO);
		holder.itemImage.setHeightRatio(heightRatio);
		holder.unfold.setVisibility(heightRatio < MAX_HEIGHT_RATIO ? View.GONE : View.VISIBLE);

		int radius = mActivity.getResources().getDimensionPixelSize(R.dimen.content_big_margin)/2;
		if(heightRatio < MAX_HEIGHT_RATIO){
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(item.getId()))
			.placeholder(R.drawable.feed_loading_default_img)
			.transform(new RoundedTopCornerTransformation(radius, 0))
			.into(holder.itemImage);
		} else {
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(item.getId()))
			.placeholder(R.drawable.feed_loading_default_img)
			.transform(new RoundedTopCornerTransformation(radius, 0))
			.resize(item.getImageWidth(), (int) (item.getImageWidth()*heightRatio)).centerCrop()
			.into(holder.itemImage);
		}

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(item.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(holder.profileImage);
	}


	private void doLike(ViewHolder holder, Item item, String likeId, int currentLikeNum, boolean isDoLike){
		isDoingLike = false;
		setLikeButton(holder, currentLikeNum, isDoLike);
		item.setPrevLikeId(likeId);
		item.setLikeItCount(isDoLike ? currentLikeNum+1 : currentLikeNum-1);
	}


	private void setLikeButton(ViewHolder holder, int currentLikeNum, boolean isDoLike){
		if(isDoLike) {
			// Do Like
			holder.likeButton.setText("" + (currentLikeNum+1));
			holder.likeButton.setActivated(true);
		} else {
			// Cancel Like
			String like = mActivity.getResources().getString(R.string.like);
			holder.likeButton.setText(--currentLikeNum == 0 ? like : ""+currentLikeNum);
			holder.likeButton.setActivated(false);
		}
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
