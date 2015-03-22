package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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

import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.LikeItDialog;
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
import com.pinthecloud.item.view.DynamicHeightImageView;

public class ItemFragment extends ItFragment implements ReplyCallback {

	private SwipeRefreshLayout mRefresh;
	private ScrollView mScrollLayout;
	private int mBaseScrollY;

	private DynamicHeightImageView mItemImage;
	private ProgressBar mProgressBar;
	private View mItemLayout;
	private TextView mContent;
	private TextView mDate;
	private Button mItButton;
	private MenuItem mItMenuButton;
	private View mItNumberLayout;
	private TextView mItNumber;

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

	private ItUser mMyItUser;
	private Item mItem;
	
	private boolean isDoingIt = false;


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
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
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
		setButton();
		setRefreshLayout();
		setScroll();
		setReplyList();

		updateItemFrag();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		setImage();
	}


	@Override
	public void onStop() {
		super.onStop();
		mItemImage.setImageBitmap(null);
		mProfileImage.setImageBitmap(null);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.item, menu);
		mItMenuButton = menu.findItem(R.id.item_menu_it);
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
		case R.id.item_menu_it:
			onClickItButton();
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
		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.item_frag_scroll_refresh);
		mScrollLayout = (ScrollView)view.findViewById(R.id.item_frag_scroll_layout);

		mItemImage = (DynamicHeightImageView)view.findViewById(R.id.item_frag_item_image);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mItemLayout = view.findViewById(R.id.item_frag_item_layout);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mItButton = (Button)view.findViewById(R.id.item_frag_it_button);
		mItNumberLayout = view.findViewById(R.id.item_frag_it_number_layout);
		mItNumber = (TextView)view.findViewById(R.id.item_frag_it_number);

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
		mUserNickName.setText(mItem.getWhoMade());
		mItemImage.setHeightRatio((double)mItem.getImageHeight()/mItem.getImageWidth());

		mReplyInputText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String reply = s.toString().trim();
				mReplyInputSubmit.setEnabled(reply.length() > 0);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	private void setButton(){
		mItButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickItButton();
			}
		});

		mItNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment likeItDialog = LikeItDialog.newInstance(mItem);
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
				Reply reply = new Reply(content, mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
				mReplyInputText.setText("");
				submitReply(reply);
			}
		});

		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoItUserPage();
			}
		});

		mUserNickName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoItUserPage();
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
				startActivity(intent);
			}
		});
	}


	private void gotoItUserPage(){
		mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.VIEW_UPLOADER, GAHelper.ITEM);

		Intent intent = new Intent(mActivity, ItUserPageActivity.class);
		intent.putExtra(ItUser.INTENT_KEY, mItem.getWhoMadeId());
		startActivity(intent);
	}


	private void setRefreshLayout(){
		int height = ViewUtil.getActionBarHeight(mActivity)/2;
		mRefresh.setColorSchemeResources(R.color.accent_color);
		mRefresh.setProgressViewOffset(true, height, ViewUtil.getActionBarHeight(mActivity)+height);
		mRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateItemFrag();
			}
		});
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

		mAimHelper.getItem(mItem, mMyItUser.getId(), new EntityCallback<Item>() {

			@Override
			public void onCompleted(Item entity) {
				if(isAdded()){
					if(entity != null){
						mProgressBar.setVisibility(View.GONE);
						mItemLayout.setVisibility(View.VISIBLE);
						mRefresh.setRefreshing(false);

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
		
		mUserDescription.setText(mItem.getWhoMadeUser().getSelfIntro());
		mUserDescription.setVisibility(!mItem.getWhoMadeUser().getSelfIntro().equals("") ? View.VISIBLE : View.GONE);
		mUserWebsite.setText(mItem.getWhoMadeUser().getWebPage());
		mUserWebsite.setVisibility(!mItem.getWhoMadeUser().getWebPage().equals("") ? View.VISIBLE : View.GONE);

		setItNumber(mItem.getLikeItCount());
		mItButton.setActivated(mItem.getPrevLikeId() != null);
		mItMenuButton.setIcon(mItem.getPrevLikeId() == null ? R.drawable.appbar_it_ic : R.drawable.appbar_it_ic_highlight);
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


	private void onClickItButton(){
		final boolean isDoIt = !mItButton.isActivated();
		final int currentItNum = Integer.parseInt(mItNumber.getText().toString());
		setItButton(currentItNum, isDoIt);

		if(isDoingIt){
			return;
		}

		isDoingIt = true;
		if(isDoIt) {
			mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.IT, GAHelper.ITEM);

			// Do it
			LikeIt it = new LikeIt(mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
			ItNotification noti = new ItNotification(mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId(),
					mItem.getWhoMade(), mItem.getWhoMadeId(), "", ItNotification.TYPE.LikeIt,
					mItem.getImageWidth(), mItem.getImageHeight());
			mAimHelper.addUnique(it, noti, new EntityCallback<LikeIt>() {

				@Override
				public void onCompleted(LikeIt entity) {
					doIt(mItem, entity.getId(), currentItNum, isDoIt);
				}
			});
		} else {
			mGaHelper.sendEvent(mThisFragment.getClass().getSimpleName(), GAHelper.IT_CANCEL, GAHelper.ITEM);

			// Cancel it
			LikeIt it = new LikeIt(mItem.getPrevLikeId());
			mAimHelper.del(it, new EntityCallback<Boolean>() {

				@Override
				public void onCompleted(Boolean entity) {
					doIt(mItem, null, currentItNum, isDoIt);
				}
			});
		}
	}


	private void doIt(Item item, String likeId, int currentItNum, boolean isDoIt){
		isDoingIt = false;
		item.setPrevLikeId(likeId);
		setItButton(currentItNum, isDoIt);

		if(isDoIt){
			// Do it
			item.setLikeItCount(currentItNum+1);
		} else {
			// Cancel it
			item.setLikeItCount(currentItNum-1);
		}
	}


	private void setItButton(int currentItNum, boolean isDoIt){
		if(isDoIt) {
			// Do it
			setItNumber(currentItNum+1);
			mItButton.setActivated(true);
			mItMenuButton.setIcon(R.drawable.appbar_it_ic_highlight);
		} else {
			// Cancel it
			setItNumber(currentItNum-1);
			mItButton.setActivated(false);
			mItMenuButton.setIcon(R.drawable.appbar_it_ic);
		}
	}


	private void setItNumber(int itNumber){
		mItNumberLayout.setVisibility(itNumber > 0 ? View.VISIBLE : View.GONE);
		mItNumber.setText(""+itNumber);
	}


	private void submitReply(final Reply reply){
		mReplyListAdapter.add(mReplyList.size(), reply);
		ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());
		mReplyListEmptyView.setVisibility(mItem.getReplyCount()+1 > 0 ? View.GONE : View.VISIBLE);

		ItNotification noti = new ItNotification(mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId(),
				mItem.getWhoMade(), mItem.getWhoMadeId(), reply.getContent(), ItNotification.TYPE.Reply,
				mItem.getImageWidth(), mItem.getImageHeight());
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
		if(mItem.getProductTagList().size() > 0){
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


	private void setImage(){
		int maxSize = mPrefHelper.getInt(ItConstant.MAX_TEXTURE_SIZE_KEY);
		if(mItem.getImageHeight() > maxSize){
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(mItem.getId()))
			.resize((int)(mItem.getImageWidth()*((float)maxSize/mItem.getImageHeight())), maxSize)
			.into(mItemImage);
		} else {
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(mItem.getId()))
			.into(mItemImage);
		}

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(mItem.getWhoMadeId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.fit()
		.into(mProfileImage);
	}
}
