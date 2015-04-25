package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItemImageActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.LikeDialog;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.GAHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ReplyCallback;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.ItLike;
import com.pinthecloud.item.model.ProductTag;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.SpanUtil;
import com.pinthecloud.item.util.ViewUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.pinthecloud.item.view.NotifyingScrollView;
import com.pinthecloud.item.view.NotifyingScrollView.OnScrollChangedListener;

public class ItemFragment extends ItFragment implements ReplyCallback {

	private NotifyingScrollView mScrollView;
	private View mItemLayout;

	private DynamicHeightImageView mItemImage;
	private HorizontalScrollView mItemImagesScrollView;
	private LinearLayout mItemImagesLayout;
	private TextView mContent;
	private TextView mUploadDate;

	private View mProgressBarLayout;
	private View mItemUpdatedLayout;

	private View mToolbarItemLayout;
	private ImageButton mLikeButton;
	private View mLikeNumberLayout;
	private TextView mLikeNumber;
	private ImageButton mMoreButton;
	private Button mProductTagButton;
	private View mProductTagCategoryLayout;
	private TextView mProductTagCategory;

	private TextView mReplyTitle;
	private TextView mReplyListEmptyView;
	private RecyclerView mReplyListView;
	private ReplyListAdapter mReplyListAdapter;
	private LinearLayoutManager mReplyListLayoutManager;
	private List<Reply> mReplyList;
	private EditText mReplyInputText;
	private Button mReplyInputSubmit;

	private CircleImageView mProfileImage;
	private TextView mUserNickName;
	private TextView mUserDescription;
	private TextView mUserWebsite;

	private int mMaxToolbarItemBottomScrollHeight;
	private View mToolbarItemBottomLayout;
	private ImageButton mLikeBottomButton;
	private View mLikeNumberBottomLayout;
	private TextView mLikeNumberBottom;
	private ImageButton mMoreBottomButton;
	private Button mProductTagBottomButton;

	private ItUser mUser;
	private Item mItem;
	private boolean isDoingLike = false;


	public static ItFragment newInstance(Item item) {
		ItFragment fragment = new ItemFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = mObjectPrefHelper.get(ItUser.class);
		mItem = getArguments().getParcelable(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item, container, false);

		mGaHelper.sendScreen(mThisFragment);
		findComponent(view);
		setComponent();
		setButton();
		setItemImageButton(mItemImage, 0);
		setToolbarItemButton();
		setScroll();
		setReplyList();

		updateItemFrag();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		setImage();
		setItemImagesView();
	}


	@Override
	public void onStop() {
		super.onStop();
		mItemImage.setImageBitmap(null);
		mProfileImage.setImageBitmap(null);
		mItemImagesLayout.removeAllViews();
	}


	@Override
	public void deleteReply(final Reply reply){
		mAimHelper.del(reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				if(!isAdded()){
					return;
				}

				mReplyListAdapter.remove(reply);
				mItem.setReplyCount(mItem.getReplyCount()-1);

				setReplyTitle(mItem.getReplyCount());
				mReplyListEmptyView.setVisibility(mItem.getReplyCount() > 0 ? View.GONE : View.VISIBLE);
				ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());
			}
		});
	}


	private void findComponent(View view){
		mScrollView = (NotifyingScrollView)view.findViewById(R.id.item_frag_scroll_view);
		mItemLayout = view.findViewById(R.id.item_frag_layout);

		mItemImage = (DynamicHeightImageView)view.findViewById(R.id.item_frag_image);
		mItemImagesScrollView = (HorizontalScrollView)view.findViewById(R.id.item_frag_images_scroll_view);
		mItemImagesLayout = (LinearLayout)view.findViewById(R.id.item_frag_images_layout);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mUploadDate = (TextView)view.findViewById(R.id.item_frag_upload_date);

		mProgressBarLayout = view.findViewById(R.id.item_frag_progress_bar_layout);
		mItemUpdatedLayout = view.findViewById(R.id.item_frag_item_updated_layout);

		mToolbarItemLayout = view.findViewById(R.id.toolbar_item_layout);
		mLikeButton = (ImageButton)mToolbarItemLayout.findViewById(R.id.toolbar_item_like_button);
		mLikeNumberLayout = mToolbarItemLayout.findViewById(R.id.toolbar_item_like_number_layout);
		mLikeNumber = (TextView)mToolbarItemLayout.findViewById(R.id.toolbar_item_like_number);
		mMoreButton = (ImageButton)mToolbarItemLayout.findViewById(R.id.toolbar_item_more);
		mProductTagButton = (Button)mToolbarItemLayout.findViewById(R.id.toolbar_item_product_tag);
		mProductTagCategoryLayout = view.findViewById(R.id.item_frag_product_tag_category_layout);
		mProductTagCategory = (TextView)view.findViewById(R.id.item_frag_product_tag_category);

		mReplyTitle = (TextView)view.findViewById(R.id.reply_frag_title);
		mReplyListEmptyView = (TextView)view.findViewById(R.id.reply_frag_list_empty_view);
		mReplyListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mReplyInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mReplyInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);

		mProfileImage = (CircleImageView)view.findViewById(R.id.item_frag_profile_image);
		mUserNickName = (TextView)view.findViewById(R.id.item_frag_user_nick_name);
		mUserDescription = (TextView)view.findViewById(R.id.item_frag_user_description);
		mUserWebsite = (TextView)view.findViewById(R.id.item_frag_user_website);

		mToolbarItemBottomLayout = view.findViewById(R.id.toolbar_item_bottom_layout);
		mLikeBottomButton = (ImageButton)mToolbarItemBottomLayout.findViewById(R.id.toolbar_item_like_button);
		mLikeNumberBottomLayout = mToolbarItemBottomLayout.findViewById(R.id.toolbar_item_like_number_layout);
		mLikeNumberBottom = (TextView)mToolbarItemBottomLayout.findViewById(R.id.toolbar_item_like_number);
		mMoreBottomButton = (ImageButton)mToolbarItemBottomLayout.findViewById(R.id.toolbar_item_more);
		mProductTagBottomButton = (Button)mToolbarItemBottomLayout.findViewById(R.id.toolbar_item_product_tag);
	}


	private void setComponent(){
		setContent();
		showMoreButton();
		mUserNickName.setText(mItem.getWhoMade());

		mReplyInputText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String reply = s.toString().trim();
				mReplyInputSubmit.setEnabled(reply.length() > 0);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	private void setButton(){
		mReplyInputSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mReplyInputText.getText().toString().trim();
				Reply reply = new Reply(content, mUser.getNickName(), mUser.getId(), mItem.getId());
				ItNotification noti = new ItNotification(mUser.getNickName(), mUser.getId(), mItem.getId(),
						mItem.getWhoMade(), mItem.getWhoMadeId(), reply.getContent(), ItNotification.TYPE.Reply,
						mItem.getImageNumber(), mItem.getMainImageWidth(), mItem.getMainImageHeight());
				submitReply(reply, noti);

				mReplyInputText.setText("");
			}
		});

		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoUserPage();
			}
		});

		mUserNickName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoUserPage();
			}
		});

		mUserWebsite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String webSite = mUserWebsite.getText().toString();
				String webSiteRegx = "(http|https)://.*";
				if(!webSite.matches(webSiteRegx)){
					webSite = "http://" + webSite;
				}

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
				mActivity.startActivity(intent);
			}
		});
	}


	private void setToolbarItemButton(){
		mLikeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLikeButton();
			}
		});

		mLikeBottomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLikeButton();
			}
		});

		mLikeNumberLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment likeDialog = LikeDialog.newInstance(mItem);
				likeDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mLikeNumberBottomLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment likeItDialog = LikeDialog.newInstance(mItem);
				likeItDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mMoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = mActivity.getResources().getStringArray(R.array.more_array);
				DialogCallback[] callbacks = getDialogCallbacks(itemList);

				ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
				listDialog.setCallbacks(callbacks);
				listDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mMoreBottomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] itemList = mActivity.getResources().getStringArray(R.array.more_array);
				DialogCallback[] callbacks = getDialogCallbacks(itemList);

				ItAlertListDialog listDialog = ItAlertListDialog.newInstance(itemList);
				listDialog.setCallbacks(callbacks);
				listDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mProductTagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.VIEW_PRODUCT_TAG, GAHelper.ITEM);

				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(mItem);
				productTagDialog.show(mThisFragment.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mProductTagBottomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.VIEW_PRODUCT_TAG, GAHelper.ITEM);

				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(mItem);
				productTagDialog.show(mThisFragment.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private DialogCallback[] getDialogCallbacks(String[] itemList){
		DialogCallback[] callbacks = new DialogCallback[itemList.length];
		callbacks[0] = new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				deleteItem();
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing
			}
			@Override
			public void doNegative(Bundle bundle) {
				// Do nothing
			}
		};
		return callbacks;
	}


	private void deleteItem(){
		mApp.showProgressDialog(mActivity);
		mAimHelper.deleteItem(mThisFragment, mItem, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				if(!isAdded()){
					return;
				}

				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.item_deleted), Toast.LENGTH_LONG).show();

				Intent intent = new Intent(mActivity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				mActivity.finish();
			}
		});
	}


	private void gotoUserPage(){
		mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.VIEW_UPLOADER, GAHelper.ITEM);

		Intent intent = new Intent(mActivity, UserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, mItem.getWhoMadeId());
		startActivity(intent);
	}


	private void setScroll(){
		final View toolbarLayout = mActivity.getToolbarLayout();
		final int actionBarHeight = ViewUtil.getActionBarHeight(mActivity);

		mScrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
		mScrollView.setOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
				// Toolbar scroll
				int diff = t - oldt;
				if(diff <= 0){ // Scroll Up, Toolbar Down
					toolbarLayout.scrollTo(0, Math.max(toolbarLayout.getScrollY()+diff, 0));
				} else { // Scroll Down, Toolbar Up
					toolbarLayout.scrollTo(0, Math.min(toolbarLayout.getScrollY()+diff, actionBarHeight));
				}

				// Toolbar item bottom scroll
				mToolbarItemBottomLayout.setVisibility(t < mMaxToolbarItemBottomScrollHeight ? View.VISIBLE : View.GONE);
			}
		});

		mItemLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mItemLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mItemLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				mScrollView.scrollTo(0, actionBarHeight);
			}
		});
	}


	private void setReplyList(){
		mReplyListView.setHasFixedSize(true);

		mReplyListLayoutManager = new LinearLayoutManager(mActivity);
		mReplyListView.setLayoutManager(mReplyListLayoutManager);
		mReplyListView.setItemAnimator(new DefaultItemAnimator());

		mReplyList = new ArrayList<Reply>();
		mReplyListAdapter = new ReplyListAdapter(mActivity, mItem, mReplyList);
		mReplyListAdapter.setReplyCallback(this);
		mReplyListView.setAdapter(mReplyListAdapter);
	}


	private void updateItemFrag(){
		mProgressBarLayout.setVisibility(View.VISIBLE);
		mItemUpdatedLayout.setVisibility(View.GONE);
		mToolbarItemBottomLayout.setVisibility(View.GONE);

		mAimHelper.getItem(mItem, mUser.getId(), new EntityCallback<Item>() {

			@Override
			public void onCompleted(Item item) {
				if(!isAdded()){
					return;
				}

				if(item != null){
					mProgressBarLayout.setVisibility(View.GONE);
					mItemUpdatedLayout.setVisibility(View.VISIBLE);

					mItem = item;
					setItemComponent();
					setReplyFrag();
					setProductTagCategory();
				} else {
					String message = getResources().getString(R.string.not_exist_item);
					ItAlertDialog notExistItemDialog = ItAlertDialog.newInstance(message, null, null, null, false, false);
					notExistItemDialog.setCallback(new DialogCallback() {

						@Override
						public void doPositive(Bundle bundle) {
							mActivity.finish();
						}
						@Override
						public void doNeutral(Bundle bundle) {
							// Do nothing
						}
						@Override
						public void doNegative(Bundle bundle) {
							// Do nothing
						}
					});
					notExistItemDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
				}
			}
		});
	}


	private void setItemComponent(){
		setContent();
		setLikeNumber(mItem.getLikeCount());
		activateToolbarItemComponent();
		setProfile();

		mToolbarItemLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mToolbarItemLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mToolbarItemLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				int deviceHeight = ViewUtil.getDeviceHeight(mActivity) - ViewUtil.getStatusBarHeight(mActivity);
				mMaxToolbarItemBottomScrollHeight = mItemLayout.getBottom() + mToolbarItemLayout.getHeight() - deviceHeight;
				showToolbarItemBottomLayout();
			}
		});
	}


	private void setContent(){
		if(mItem.getContent() != null){
			mContent.setText(SpanUtil.getSpannedBody(mActivity, mItem.getContent(), '#'));
		}

		if(mItem.getRawCreateDateTime() != null){
			String elapsedTime = mItem.getCreateDateTime().getElapsedTimeString(mActivity);
			String uploadedOn = String.format(Locale.US, mActivity.getResources().getString(R.string.uploaded_on), elapsedTime);
			mUploadDate.setText(uploadedOn);	
		}
	}


	private void setReplyFrag(){
		mReplyList.clear();
		mReplyListAdapter.addAll(mItem.getReplyList());

		final int displayReplyNum = getResources().getInteger(R.integer.item_display_reply_num);
		mReplyListAdapter.setHasPrevious(mItem.getReplyCount() > displayReplyNum ? true : false);
		mReplyListEmptyView.setVisibility(mItem.getReplyCount() > 0 ? View.GONE : View.VISIBLE);
		setReplyTitle(mItem.getReplyCount());

		mReplyListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mReplyListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mReplyListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				ViewUtil.setListHeightBasedOnChildren(mReplyListView, Math.min(mItem.getReplyCount(), displayReplyNum+1));
			}
		});
	}


	private void setProductTagCategory(){
		if(mItem.isHasProductTag()){
			mProductTagCategoryLayout.setVisibility(View.VISIBLE);
			mProductTagCategory.setText(getProductTagCategoryString(mItem.getProductTagList()));
		} else {
			mProductTagCategoryLayout.setVisibility(View.GONE);
		}
	}


	private void setReplyTitle(int replyCount){
		String title = getResources().getString(R.string.comments) + (replyCount > 0 ? " " + replyCount : "");
		mReplyTitle.setText(title);
	}


	private void submitReply(final Reply reply, ItNotification noti){
		mReplyListAdapter.add(mReplyList.size(), reply);
		mReplyListEmptyView.setVisibility(mItem.getReplyCount()+1 > 0 ? View.GONE : View.VISIBLE);
		ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());

		mAimHelper.add(reply, noti, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply addedReply) {
				if(!isAdded()){
					return;
				}

				mItem.setReplyCount(mItem.getReplyCount()+1);
				setReplyTitle(mItem.getReplyCount());
				mReplyListAdapter.replace(mReplyList.indexOf(reply), addedReply);
			}
		});
	}


	private void showToolbarItemBottomLayout(){
		if(mMaxToolbarItemBottomScrollHeight - mScrollView.getScrollY() > 0){
			mToolbarItemBottomLayout.setVisibility(View.VISIBLE);
			Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_up);
			mToolbarItemBottomLayout.startAnimation(anim);
		} else {
			mToolbarItemBottomLayout.setVisibility(View.GONE);
		}
	}


	private void activateToolbarItemComponent(){
		mLikeButton.setActivated(mItem.getPrevLikeId() != null);
		mLikeBottomButton.setActivated(mItem.getPrevLikeId() != null);
		mProductTagButton.setActivated(mItem.isHasProductTag());
		mProductTagBottomButton.setActivated(mItem.isHasProductTag());
	}


	private void onClickLikeButton(){
		final boolean isDoLike = !mLikeButton.isActivated();
		final int currentLikeNum = Integer.parseInt(mLikeNumber.getText().toString());
		setLikeButton(currentLikeNum, isDoLike);

		if(isDoingLike){
			return;
		}

		isDoingLike = true;
		if(isDoLike) {
			mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.LIKE, GAHelper.ITEM);

			// Do Like
			ItLike like = new ItLike(mUser.getNickName(), mUser.getId(), mItem.getId());
			ItNotification noti = new ItNotification(mUser.getNickName(), mUser.getId(), mItem.getId(),
					mItem.getWhoMade(), mItem.getWhoMadeId(), "", ItNotification.TYPE.ItLike,
					mItem.getImageNumber(), mItem.getMainImageWidth(), mItem.getMainImageHeight());
			mAimHelper.addUnique(like, noti, new EntityCallback<ItLike>() {

				@Override
				public void onCompleted(ItLike entity) {
					doLike(mItem, entity.getId(), currentLikeNum, isDoLike);
				}
			});
		} else {
			mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.LIKE_CANCEL, GAHelper.ITEM);

			// Cancel Like
			ItLike like = new ItLike(mItem.getPrevLikeId());
			mAimHelper.del(like, new EntityCallback<Boolean>() {

				@Override
				public void onCompleted(Boolean entity) {
					doLike(mItem, null, currentLikeNum, isDoLike);
				}
			});
		}
	}


	private void doLike(Item item, String likeId, int currentLikeNum, boolean isDoLike){
		isDoingLike = false;
		setLikeButton(currentLikeNum, isDoLike);
		item.setPrevLikeId(likeId);
		item.setLikeCount(isDoLike ? currentLikeNum+1 : currentLikeNum-1);
	}


	private void setLikeButton(int currentLikeNum, boolean isDoLike){
		setLikeNumber(isDoLike ? currentLikeNum+1 : currentLikeNum-1);
		mLikeButton.setActivated(isDoLike);
		mLikeBottomButton.setActivated(isDoLike);
	}


	private void setLikeNumber(int likeNumber){
		mLikeNumberLayout.setVisibility(likeNumber > 0 ? View.VISIBLE : View.GONE);
		mLikeNumberBottomLayout.setVisibility(likeNumber > 0 ? View.VISIBLE : View.GONE);
		mLikeNumber.setText(""+likeNumber);
		mLikeNumberBottom.setText(""+likeNumber);
	}


	private void showMoreButton(){
		if(mItem.checkMine() || mApp.isAdmin()){
			mMoreButton.setVisibility(View.VISIBLE);
			mMoreBottomButton.setVisibility(View.VISIBLE);
		} else {
			mMoreButton.setVisibility(View.GONE);
			mMoreBottomButton.setVisibility(View.GONE);
		}
	}


	private String getProductTagCategoryString(List<ProductTag> tagList){
		String categoryString = "";
		List<Integer> categoryList = new ArrayList<Integer>();
		for(ProductTag tag : tagList){
			if(!categoryList.contains(tag.getCategory())){
				categoryList.add(tag.getCategory());
				categoryString = categoryString + (categoryList.size()==1 ? "" : ", ") + tag.categoryString(mActivity);
			}
		}
		return categoryString;
	}


	private void setProfile(){
		mUserDescription.setText(mItem.getWhoMadeUser().getSelfIntro());
		mUserDescription.setVisibility(mItem.getWhoMadeUser().getSelfIntro().equals("") ? View.GONE : View.VISIBLE);
		mUserWebsite.setText(mItem.getWhoMadeUser().getWebPage());
		mUserWebsite.setVisibility(mItem.getWhoMadeUser().getWebPage().equals("") ? View.GONE : View.VISIBLE);
	}


	private void setImage(){
		mItemImage.setHeightRatio((double)mItem.getCoverImageHeight()/mItem.getCoverImageWidth());
		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImageUrl(mItem.getId()))
		.placeholder(R.drawable.feed_loading_default_img)
		.into(mItemImage);

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileUrl(mItem.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(mProfileImage);
	}


	private void setItemImagesView(){
		mItemImagesScrollView.setVisibility(mItem.getImageNumber() > 1 ? View.VISIBLE : View.GONE);

		for(int i=1 ; i<mItem.getImageNumber() ; i++){
			ImageView image = getItemImageView();
			setItemImageButton(image, i);
			mItemImagesLayout.addView(image);

			String imageId = mItem.getId() + "_" + i;
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImageUrl(imageId+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX))
			.placeholder(R.drawable.feed_loading_default_img)
			.fit()
			.into(image);
		}
	}


	private ImageView getItemImageView(){
		int width = getResources().getDimensionPixelSize(R.dimen.item_thumbnail_image_width);
		int margin = getResources().getDimensionPixelSize(R.dimen.content_margin);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
		layoutParams.setMargins(0, 0, margin, 0);

		RoundedImageView image = new RoundedImageView(mActivity);
		image.setLayoutParams(layoutParams);
		image.setCornerRadius(R.dimen.content_margin);
		image.setSquare(true);
		return image;
	}


	private void setItemImageButton(ImageView image, final int index){
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItemImageActivity.class);
				intent.putExtra(Item.INTENT_KEY, mItem);
				intent.putExtra(ItemImageActivity.POSITION_KEY, index);
				startActivity(intent);
			}
		});
	}
}
