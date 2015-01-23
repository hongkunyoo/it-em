package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ProductTagDialog;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.interfaces.ReplyCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.util.ViewUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.DynamicHeightImageView;
import com.pinthecloud.item.view.ExpandableHeightRecyclerView;

public class ItemFragment extends ItFragment implements ReplyCallback {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	private ScrollView mScrollLayout;
	private int mBaseScrollY;

	private DynamicHeightImageView mItemImage;
	private ProgressBar mProgressBar;
	private LinearLayout mItemLayout;
	private TextView mContent;
	private TextView mDate;
	private ImageButton mItButton;
	private LinearLayout mItNumberLayout;
	private TextView mItNumber;

	private LinearLayout mProductTagLayout;

	private TextView mReplyTitle;
	private TextView mReplyListEmptyView;
	private ExpandableHeightRecyclerView mReplyListView;
	private ReplyListAdapter mReplyListAdapter;
	private LinearLayoutManager mReplyListLayoutManager;
	private List<Reply> mReplyList;

	private EditText mReplyInputText;
	private Button mReplyInputSubmit;

	private LinearLayout mProfileLayout;
	private CircleImageView mProfileImage;
	private TextView mNickName;

	private ItUser mMyItUser;
	private Item mItem;

	private boolean isDoingLikeIt = false;


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

		setHasOptionsMenu(true);
		findComponent(view);
		setToolbar();
		setComponent();
		setScroll();
		setButton();
		setImageView();
		setReplyList();
		setText();
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
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem deleteMenuItem = menu.findItem(R.id.item_delete);
		deleteMenuItem.setVisible(mItem.checkIsMine());
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.item_delete:
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
				mReplyListAdapter.remove(reply);
				mItem.setReplyCount(mItem.getReplyCount()-1);

				setReplyTitle(mItem.getReplyCount());
				ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());
				showReplyEmptyView(mItem.getReplyCount());
			}
		});
	}


	private void findComponent(View view){
		mToolbarLayout = view.findViewById(R.id.item_frag_toolbar_layout);
		mToolbar = (Toolbar)view.findViewById(R.id.toolbar);
		mScrollLayout = (ScrollView)view.findViewById(R.id.item_frag_scroll_layout);

		mItemImage = (DynamicHeightImageView)view.findViewById(R.id.item_frag_item_image);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mItemLayout = (LinearLayout)view.findViewById(R.id.item_frag_item_layout);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mItButton = (ImageButton)view.findViewById(R.id.item_frag_it_button);
		mItNumberLayout = (LinearLayout)view.findViewById(R.id.item_frag_it_number_layout);
		mItNumber = (TextView)view.findViewById(R.id.item_frag_it_number);

		mProductTagLayout = (LinearLayout)view.findViewById(R.id.item_frag_product_tag_layout);
		mReplyTitle = (TextView)view.findViewById(R.id.reply_frag_title);
		mReplyListEmptyView = (TextView)view.findViewById(R.id.reply_frag_list_empty_view);
		mReplyListView = (ExpandableHeightRecyclerView)view.findViewById(R.id.reply_frag_list);
		mReplyInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mReplyInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);

		mProfileLayout = (LinearLayout)view.findViewById(R.id.item_frag_profile_layout);
		mProfileImage = (CircleImageView)view.findViewById(R.id.item_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.item_frag_nick_name);
	}


	private void setToolbar(){
		mActivity.setSupportActionBar(mToolbar);

		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mItem.getWhoMade() + getResources().getString(R.string.of) 
				+ " " + getResources().getString(R.string.app_name));

		mToolbarLayout.bringToFront();
	}


	private void setComponent(){
		setItNumber(mItem.getLikeItCount());

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


	private void setScroll(){
		final int actionBarHeight = ViewUtil.getActionBarHeight(mActivity);
		mScrollLayout.scrollTo(0, actionBarHeight);
		mScrollLayout.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public void onScrollChanged() {
				int currentScrollY = mScrollLayout.getScrollY();
				if(currentScrollY >= 0){
					int diffY = currentScrollY-mBaseScrollY;
					if(diffY < 0){
						// Scroll Up, Toolbar Down
						mToolbarLayout.scrollTo(0, Math.max(mToolbarLayout.getScrollY()+diffY, 0));
					} else if(diffY > 0) {
						// Scroll Down, Toolbar Up
						mToolbarLayout.scrollTo(0, Math.min(mToolbarLayout.getScrollY()+diffY, actionBarHeight));
					}
					mBaseScrollY = currentScrollY;
				}
			}
		});
	}


	private void setButton(){
		mItButton.setActivated(mItem.getPrevLikeId() != null);
		mItButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final boolean isDoLike = !mItButton.isActivated();
				final int currentLikeItNum = Integer.parseInt(mItNumber.getText().toString());
				setItButton(currentLikeItNum, isDoLike);

				if(!isDoingLikeIt){
					isDoingLikeIt = true;

					if(isDoLike) {
						// Do like it
						LikeIt likeIt = new LikeIt(mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
						mApp.getAimHelper().addUnique(likeIt, new EntityCallback<LikeIt>() {

							@Override
							public void onCompleted(LikeIt entity) {
								doLikeIt(mItem, entity.getId(), currentLikeItNum, isDoLike);
							}
						});
					} else {
						// Cancel like it
						LikeIt likeIt = new LikeIt(mItem.getPrevLikeId());
						mApp.getAimHelper().del(likeIt, new EntityCallback<Boolean>() {

							@Override
							public void onCompleted(Boolean entity) {
								doLikeIt(mItem, null, currentLikeItNum, isDoLike);
							}
						});
					}
				}
			}
		});

		mReplyInputSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Reply reply = new Reply(mReplyInputText.getText().toString().trim(),
						mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
				mReplyInputText.setText("");
				submitReply(reply);
			}
		});

		mProfileLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItUserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, mItem.getWhoMadeId());
				startActivity(intent);
			}
		});
	}


	private void setImageView(){
		mItemImage.setHeightRatio((double)mItem.getImageHeight()/mItem.getImageWidth());
	}


	private void setReplyList(){
		mReplyListView.setHasFixedSize(true);
		mReplyListView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.reply_row_previous_height);

		mReplyListLayoutManager = new LinearLayoutManager(mActivity);
		mReplyListView.setLayoutManager(mReplyListLayoutManager);
		mReplyListView.setItemAnimator(new DefaultItemAnimator());

		mReplyList = new ArrayList<Reply>();
		mReplyListAdapter = new ReplyListAdapter(mActivity, mItem, mReplyList);
		mReplyListAdapter.setReplyCallback(this);
		mReplyListView.setAdapter(mReplyListAdapter);
	}


	private void setText(){
		mContent.setText(mItem.getContent());
		mDate.setText(mItem.getCreateDateTime().getElapsedDateTime(getResources()));
		mItNumber.setText(""+mItem.getLikeItCount());
		mNickName.setText(mItem.getWhoMade());
	}


	private void updateItemFrag(){
		mProgressBar.setVisibility(View.VISIBLE);
		mItemLayout.setVisibility(View.GONE);

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(2);
				updateProductTag(frag);
				updateRecentReplyList(frag);
			}
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				mProgressBar.setVisibility(View.GONE);
				mItemLayout.setVisibility(View.VISIBLE);
			}
		});
	}


	private void updateProductTag(ItFragment frag) {
		mProductTagLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment productTagDialog = ProductTagDialog.newInstance(mItem);
				productTagDialog.show(mThisFragment.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		AsyncChainer.notifyNext(frag);
	}


	private void updateRecentReplyList(final ItFragment frag) {
		mAimHelper.listRecent(Reply.class, mItem.getId(), new ListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				if(isAdded()){
					// Add reply item
					mReplyList.clear();
					mReplyListAdapter.addAll(list);
					mItem.setReplyCount(count);

					// Set see previous row
					int displayReplyNum = getResources().getInteger(R.integer.item_display_reply_num);
					if(mItem.getReplyCount() > displayReplyNum){
						mReplyListAdapter.setHasPrevious(true);
					} else {
						mReplyListAdapter.setHasPrevious(false);
					}

					// Set reply list expand setting for expand when on draw
					mReplyListView.setOnDrawExpandRowCount(Math.min(mItem.getReplyCount(), displayReplyNum+1));

					// Set reply list fragment
					showReplyEmptyView(mItem.getReplyCount());
					setReplyTitle(mItem.getReplyCount());

					AsyncChainer.notifyNext(frag);
				} else {
					AsyncChainer.clearChain(frag);
				}
			}
		});
	}


	private void showReplyEmptyView(int replyCount){
		if(replyCount > 0){
			mReplyListEmptyView.setVisibility(View.GONE);
		} else {
			mReplyListEmptyView.setVisibility(View.VISIBLE);
		}
	}


	private void setImage(){
		int maxSize = mPrefHelper.getInt(ImageUtil.MAX_TEXTURE_SIZE_KEY);
		if(mItem.getImageHeight() > maxSize){
			mApp.getPicasso()
			.load(BlobStorageHelper.getItemImgUrl(mItem.getId()))
			.placeholder(R.drawable.feed_loading_default_img)
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


	private void submitReply(final Reply reply){
		mReplyListAdapter.add(mReplyList.size(), reply);
		ViewUtil.setListHeightBasedOnChildren(mReplyListView, mReplyListAdapter.getItemCount());
		showReplyEmptyView(mItem.getReplyCount()+1);

		mAimHelper.add(reply, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				mItem.setReplyCount(mItem.getReplyCount()+1);
				setReplyTitle(mItem.getReplyCount());

				mReplyListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
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


	private void doLikeIt(Item item, String likeItId, int currentLikeItNum, boolean isDoLikeIt){
		isDoingLikeIt = false;
		item.setPrevLikeId(likeItId);
		setItButton(currentLikeItNum, isDoLikeIt);

		if(isDoLikeIt){
			// Do like it
			item.setLikeItCount(currentLikeItNum+1);
		} else {
			// Cancel like it
			item.setLikeItCount(currentLikeItNum-1);
		}
	}


	private void setItButton(int currentLikeItNum, boolean isDoLikeIt){
		if(isDoLikeIt) {
			// Do like it
			setItNumber(currentLikeItNum+1);
			mItButton.setActivated(true);
		} else {
			// Cancel like it
			setItNumber(currentLikeItNum-1);
			mItButton.setActivated(false);
		}
	}


	private void setItNumber(int itNumber){
		if(itNumber <= 0){
			mItNumberLayout.setVisibility(View.GONE);
		} else {
			mItNumberLayout.setVisibility(View.VISIBLE);
		}
		mItNumber.setText(""+itNumber);
	}


	private void setReplyTitle(int replyCount){
		String title = getResources().getString(R.string.comments);
		if(replyCount != 0){
			title = title + " " + replyCount;
		}
		mReplyTitle.setText(title);
	}
}
