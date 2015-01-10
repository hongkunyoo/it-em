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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.dialog.ReplyDialog;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.ViewHolder> {

	private ItActivity mActivity;
	private ItFragment mFrag;
	private List<Item> mItemList;


	public HomeItemListAdapter(ItActivity activity, ItFragment frag, List<Item> itemList) {
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;

		public RelativeLayout profileLayout;
		public CircleImageView profileImage;
		public TextView nickName;
		public Button more;

		public ImageView itemImage;
		public TextView content;
		public ImageButton itButton;
		public TextView itNumber;
		public LinearLayout reply;
		public TextView replyNumber;
		public LinearLayout productTag;
		public TextView productTagNumber;

		public ViewHolder(View view) {
			super(view);
			this.view = view;

			this.profileLayout = (RelativeLayout)view.findViewById(R.id.row_home_item_list_profile_layout);
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_home_item_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_home_item_list_nick_name);
			this.more = (Button)view.findViewById(R.id.row_home_item_list_more);

			this.itemImage = (ImageView)view.findViewById(R.id.row_home_item_list_item_image);
			this.content = (TextView)view.findViewById(R.id.row_home_item_list_content);
			this.itButton = (ImageButton)view.findViewById(R.id.row_home_item_list_it_button);
			this.itNumber = (TextView)view.findViewById(R.id.row_home_item_list_it_number);
			this.reply = (LinearLayout)view.findViewById(R.id.row_home_item_list_reply);
			this.replyNumber = (TextView)view.findViewById(R.id.row_home_item_list_reply_number);
			this.productTag = (LinearLayout)view.findViewById(R.id.row_home_item_list_product_tag);
			this.productTagNumber = (TextView)view.findViewById(R.id.row_home_item_list_product_tag_number);
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
		holder.content.setText(item.getContent());
		holder.itNumber.setText(""+item.getLikeItCount());
		holder.nickName.setText(item.getWhoMade());
	}


	private void setButton(final ViewHolder holder, final Item item){
		holder.profileLayout.setOnClickListener(new OnClickListener() {

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
					ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
					listDialog.show(mFrag.getFragmentManager(), ItDialogFragment.INTENT_KEY);
				}
			});
		} else {
			holder.more.setVisibility(View.GONE);
			holder.more.setOnClickListener(null);
		}

		holder.itemImage.setOnClickListener(new OnClickListener() {

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
				final int likeItNum = (Integer.parseInt(holder.itNumber.getText().toString()) + 1);
				holder.itNumber.setText(String.valueOf(likeItNum));

				ItApplication app = ItApplication.getInstance();
				AimHelper mAimHelper = app.getAimHelper();
				ItUser myItUser = app.getObjectPrefHelper().get(ItUser.class);

				LikeIt likeIt = new LikeIt(myItUser.getNickName(), myItUser.getId(), item.getId());
				mAimHelper.add(likeIt, new EntityCallback<LikeIt>() {

					@Override
					public void onCompleted(LikeIt entity) {
						item.setLikeItCount(likeItNum);
					}
				});
			}
		});

		holder.replyNumber.setText(""+item.getReplyCount());
		holder.reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReplyDialog replyDialog = new ReplyDialog(mFrag, item);
				replyDialog.show(mFrag.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		holder.productTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProductTagDialog productTagDialog = new ProductTagDialog(mFrag, item);
				productTagDialog.show(mFrag.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setImageView(final ViewHolder holder, Item item, int position) {
		Picasso.with(holder.itemImage.getContext())
		.load(BlobStorageHelper.getItemImgUrl(item.getId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.into(holder.itemImage);

		Picasso.with(holder.profileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(item.getWhoMadeId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
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
		AsyncChainer.asyncChain(mFrag, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(3);

				ItApplication app = ItApplication.getInstance();
				AimHelper aimHelper = app.getAimHelper();
				BlobStorageHelper blobStorageHelper = app.getBlobStorageHelper();

				aimHelper.delItem(item, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag);
					}
				});

				blobStorageHelper.deleteBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId(), new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag);
					}
				});
				
				blobStorageHelper.deleteBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId()+BitmapUtil.SMALL_POSTFIX, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag);
					}
				});
			}

		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
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
