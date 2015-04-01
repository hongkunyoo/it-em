package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.adapter.ItemImagePagerAdapter;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.LikeDialog;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ReplyCallback;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.model.ProductTag;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.TextUtil;
import com.pinthecloud.item.util.ViewUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightViewPager;

public class ItemFragment extends ItFragment implements ReplyCallback {

	private ScrollView mScrollLayout;
	private int mBaseScrollY;

	private DynamicHeightViewPager mImagePager;
	private ItemImagePagerAdapter mImagePagerAdapter;
	private TextView mImageNumber;

	private ProgressBar mProgressBar;
	private View mItemLayout;
	private TextView mContent;
	private TextView mDate;
	private Button mLikeButton;
	private View mLikeNumberLayout;
	private TextView mLikeNumber;

	private View mProductTagLayout;
	private TextView mProductTagEmptyView;
	private View mProductTagTextLayout;
	private TextView mProductTagText;

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
		setHasOptionsMenu(true);

		findComponent(view);
		setComponent();
		setImagePager();
		setButton();
		setScroll();
		setReplyList();

		updateItemFrag();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		setProfileImage();
	}


	@Override
	public void onStop() {
		super.onStop();
		mProfileImage.setImageBitmap(null);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.item, menu);
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem deleteMenuItem = menu.findItem(R.id.item_menu_delete);
		deleteMenuItem.setVisible(mItem.checkMine() || mApp.isAdmin());
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
		case R.id.item_menu_delete:
			deleteItem(mItem);
			break;
		}
		return super.onOptionsItemSelected(menu);
	}


	@Override
	public void deleteReply(final Reply reply){
		mAimHelper.del(reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				if(isAdded()){
					mReplyListAdapter.remove(reply);
					mItem.setReplyCount(mItem.getReplyCount()-1);

					setReplyTitle(mItem.getReplyCount());
					ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());
					mReplyListEmptyView.setVisibility(mItem.getReplyCount() > 0 ? View.GONE : View.VISIBLE);
				}
			}
		});
	}


	private void findComponent(View view){
		mScrollLayout = (ScrollView)view.findViewById(R.id.item_frag_scroll_layout);

		mImagePager = (DynamicHeightViewPager)view.findViewById(R.id.item_frag_image_pager);
		mImageNumber = (TextView)view.findViewById(R.id.item_frag_image_number);

		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mItemLayout = view.findViewById(R.id.item_frag_item_layout);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mLikeButton = (Button)view.findViewById(R.id.item_frag_like_button);
		mLikeNumberLayout = view.findViewById(R.id.item_frag_like_number_layout);
		mLikeNumber = (TextView)view.findViewById(R.id.item_frag_like_number);

		mProductTagLayout = view.findViewById(R.id.item_frag_product_tag_layout);
		mProductTagEmptyView = (TextView)view.findViewById(R.id.item_frag_product_tag_empty_view);
		mProductTagTextLayout = view.findViewById(R.id.item_frag_product_tag_text_layout);
		mProductTagText = (TextView)view.findViewById(R.id.item_frag_product_tag_text);

		mReplyTitle = (TextView)view.findViewById(R.id.reply_frag_title);
		mReplyListEmptyView = (TextView)view.findViewById(R.id.reply_frag_list_empty_view);
		mReplyListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mReplyInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mReplyInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);

		mProfileImage = (CircleImageView)view.findViewById(R.id.item_frag_profile_image);
		mUserNickName = (TextView)view.findViewById(R.id.item_frag_user_nick_name);
		mUserDescription = (TextView)view.findViewById(R.id.item_frag_user_description);
		mUserWebsite = (TextView)view.findViewById(R.id.item_frag_user_website);
	}


	private void setComponent(){
		mImageNumber.setText("1/" + mItem.getImageNumber());
		mImageNumber.setVisibility(mItem.getImageNumber() > 1 ? View.VISIBLE : View.GONE);
		
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


	private void setImagePager(){
		mImagePagerAdapter = new ItemImagePagerAdapter(mActivity, mItem);
		mImagePager.setOffscreenPageLimit(mImagePagerAdapter.getCount());
		mImagePager.setAdapter(mImagePagerAdapter);
		mImagePager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageSelected(int position) {
				mImageNumber.setText((position+1) + "/" + mItem.getImageNumber());
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}


	private void setButton(){
		mLikeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLikeButton();
			}
		});

		mLikeNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment likeItDialog = LikeDialog.newInstance(mItem);
				likeItDialog.show(mActivity.getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mProductTagLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.VIEW_PRODUCT_TAG, GAHelper.ITEM);

				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(mItem, (ArrayList<ProductTag>)mItem.getProductTagList());
				productTagDialog.show(mThisFragment.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mReplyInputSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mReplyInputText.getText().toString().trim();
				Reply reply = new Reply(content, mUser.getNickName(), mUser.getId(), mItem.getId());
				mReplyInputText.setText("");
				submitReply(reply);
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


	private void gotoUserPage(){
		mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.VIEW_UPLOADER, GAHelper.ITEM);

		Intent intent = new Intent(mActivity, UserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, mItem.getWhoMadeId());
		startActivity(intent);
	}


	private void setScroll(){
		final View toolbarLayout = mActivity.getToolbarLayout();
		final int actionBarHeight = ViewUtil.getActionBarHeight(mActivity);
		mScrollLayout.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public void onScrollChanged() {
				int currentScrollY = mScrollLayout.getScrollY();
				if(currentScrollY >= 0){
					int diffY = currentScrollY-mBaseScrollY;
					if(diffY < 0){
						// Scroll Up, Toolbar Down
						toolbarLayout.scrollTo(0, Math.max(toolbarLayout.getScrollY()+diffY, 0));
					} else if(diffY > 0) {
						// Scroll Down, Toolbar Up
						toolbarLayout.scrollTo(0, Math.min(toolbarLayout.getScrollY()+diffY, actionBarHeight));
					}
					mBaseScrollY = currentScrollY;
				}
			}
		});

		mScrollLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mScrollLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mScrollLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				mScrollLayout.scrollTo(0, ViewUtil.getActionBarHeight(mActivity));
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
		mProgressBar.setVisibility(View.VISIBLE);
		mItemLayout.setVisibility(View.GONE);

		mAimHelper.getItem(mItem, mUser.getId(), new EntityCallback<Item>() {

			@Override
			public void onCompleted(Item entity) {
				if(isAdded()){
					if(entity != null){
						mProgressBar.setVisibility(View.GONE);
						mItemLayout.setVisibility(View.VISIBLE);

						mItem = entity;
						setItemComponent();
						setProductTagFrag();
						setReplyFrag();
					} else {
						String message = getResources().getString(R.string.not_exist_item);
						ItAlertDialog notExistItemDialog = ItAlertDialog.newInstance(message, null, null, false);
						notExistItemDialog.setCallback(new DialogCallback() {

							@Override
							public void doPositiveThing(Bundle bundle) {
								mActivity.finish();
							}
							@Override
							public void doNegativeThing(Bundle bundle) {
								// Do nothing
							}
						});
						notExistItemDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
					}
				}
			}
		});
	}


	private void setItemComponent(){
		mContent.setText(TextUtil.getBody(mActivity, mItem.getContent()));
		mDate.setText(mItem.getCreateDateTime().getElapsedDateTime(mActivity));
		
		setLikeNumber(mItem.getLikeItCount());
		mLikeButton.setActivated(mItem.getPrevLikeId() != null);
		
		mUserDescription.setText(mItem.getWhoMadeUser().getSelfIntro());
		mUserDescription.setVisibility(!mItem.getWhoMadeUser().getSelfIntro().equals("") ? View.VISIBLE : View.GONE);
		mUserWebsite.setText(mItem.getWhoMadeUser().getWebPage());
		mUserWebsite.setVisibility(!mItem.getWhoMadeUser().getWebPage().equals("") ? View.VISIBLE : View.GONE);
	}


	private void deleteItem(final Item item){
		mApp.showProgressDialog(mActivity);
		mAimHelper.delItem(mThisFragment, item, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				mApp.dismissProgressDialog();

				Intent intent = new Intent(mActivity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
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
			LikeIt like = new LikeIt(mUser.getNickName(), mUser.getId(), mItem.getId());
			ItNotification noti = new ItNotification(mUser.getNickName(), mUser.getId(), mItem.getId(),
					mItem.getWhoMade(), mItem.getWhoMadeId(), "", ItNotification.TYPE.LikeIt,
					mItem.getImageNumber(), mItem.getImageWidth(), mItem.getImageHeight());
			mAimHelper.addUnique(like, noti, new EntityCallback<LikeIt>() {

				@Override
				public void onCompleted(LikeIt entity) {
					doLike(mItem, entity.getId(), currentLikeNum, isDoLike);
				}
			});
		} else {
			mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.LIKE_CANCEL, GAHelper.ITEM);

			// Cancel Like
			LikeIt like = new LikeIt(mItem.getPrevLikeId());
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
		item.setLikeItCount(isDoLike ? currentLikeNum+1 : currentLikeNum-1);
	}


	private void setLikeButton(int currentLikeNum, boolean isDoLike){
		setLikeNumber(isDoLike ? currentLikeNum+1 : currentLikeNum-1);
		mLikeButton.setActivated(isDoLike);
	}


	private void setLikeNumber(int likeNumber){
		mLikeNumberLayout.setVisibility(likeNumber > 0 ? View.VISIBLE : View.GONE);
		mLikeNumber.setText(""+likeNumber);
	}


	private void submitReply(final Reply reply){
		mReplyListAdapter.add(mReplyList.size(), reply);
		ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());
		mReplyListEmptyView.setVisibility(mItem.getReplyCount()+1 > 0 ? View.GONE : View.VISIBLE);

		ItNotification noti = new ItNotification(mUser.getNickName(), mUser.getId(), mItem.getId(),
				mItem.getWhoMade(), mItem.getWhoMadeId(), reply.getContent(), ItNotification.TYPE.Reply,
				mItem.getImageNumber(), mItem.getImageWidth(), mItem.getImageHeight());
		mAimHelper.add(reply, noti, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				mItem.setReplyCount(mItem.getReplyCount()+1);
				setReplyTitle(mItem.getReplyCount());

				mReplyListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
	}


	private void setReplyTitle(int replyCount){
		String title = getResources().getString(R.string.comments);
		if(replyCount != 0){
			title = title + " " + replyCount;
		}
		mReplyTitle.setText(title);
	}


	private String getProductTagCategoryText(List<ProductTag> list){
		String categoryString = "";
		List<String> categoryList = new ArrayList<String>();
		for(ProductTag tag : list){
			if(!categoryList.contains(tag.categoryString(mActivity))){
				categoryList.add(tag.categoryString(mActivity));
				categoryString = categoryString + (categoryList.size()==1 ? "" : ", ") + tag.categoryString(mActivity);
			}
		}
		return categoryString;
	}


	private void setProductTagFrag(){
		if(mItem.isHasProductTag()){
			mProductTagEmptyView.setVisibility(View.GONE);
			mProductTagTextLayout.setVisibility(View.VISIBLE);

			mProductTagLayout.setEnabled(true);
			mProductTagLayout.setActivated(true);
			mProductTagText.setText(getProductTagCategoryText(mItem.getProductTagList()));
		} else {
			mProductTagEmptyView.setVisibility(View.VISIBLE);
			mProductTagTextLayout.setVisibility(View.GONE);

			mProductTagLayout.setEnabled(false);
			mProductTagLayout.setActivated(false);
			mProductTagText.setText("");
		}
	}


	private void setReplyFrag(){
		// Add replys
		mReplyList.clear();
		mReplyListAdapter.addAll(mItem.getReplyList());

		// Set see previous row
		final int displayReplyNum = getResources().getInteger(R.integer.item_display_reply_num);
		if(mItem.getReplyCount() > displayReplyNum){
			mReplyListAdapter.setHasPrevious(true);
		} else {
			mReplyListAdapter.setHasPrevious(false);
		}

		// Set reply list height for scrollview
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

		// Set reply list fragment
		mReplyListEmptyView.setVisibility(mItem.getReplyCount() > 0 ? View.GONE : View.VISIBLE);
		setReplyTitle(mItem.getReplyCount());
	}


	private void setProfileImage(){
		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(mItem.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(mProfileImage);
	}
}
