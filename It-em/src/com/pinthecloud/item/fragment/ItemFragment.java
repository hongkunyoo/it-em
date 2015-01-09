package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
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
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.SquareImageView;
import com.squareup.picasso.Picasso;

public class ItemFragment extends ItFragment implements ReplyCallback {

	private final int DISPLAY_REPLY_COUNT = 2;

	private ProgressBar mProgressBar;
	private ObservableScrollView mScrollView;
	private SquareImageView mImage;
	private TextView mContent;
	private TextView mDate;
	private ImageButton mItButton;
	private TextView mItNumber;
	private RelativeLayout mProductTag;

	private TextView mReplyTitle;
	private View mReplyTitlebarDivider;
	private FrameLayout mReplyListLayout;
	private RecyclerView mReplyListView;
	private ReplyListAdapter mReplyListAdapter;
	private LinearLayoutManager mReplyListLayoutManager;
	private List<Reply> mReplyList;
	private LinearLayout mReplyListEmptyView;
	private EditText mReplyInputText;
	private Button mReplyInputSubmit;

	private LinearLayout mProfileLayout;
	private CircleImageView mProfileImage;
	private TextView mNickName;

	private ItUser mMyItUser;
	private Item mItem;


	public static ItemFragment newInstance(Item item) {
		ItemFragment fragment = new ItemFragment();
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
		setComponent();
		setButton();
		setScrollView();
		setReplyList();
		setText();

		updateItemFrag();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		setImageView();
	}


	@Override
	public void onStop() {
		super.onStop();
		mImage.setImageBitmap(null);
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
		mAimHelper.del(mThisFragment, reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				mItem.setReplyCount(mItem.getReplyCount()-1);
				setReplyTitle();
				resizeReplyListLayoutHeight(mItem.getReplyCount());
				showReplyList(mItem.getReplyCount());

				mReplyListAdapter.remove(reply);
			}
		});
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mScrollView = (ObservableScrollView)view.findViewById(R.id.item_frag_scroll_layout);
		mImage = (SquareImageView)view.findViewById(R.id.item_frag_image);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mItButton = (ImageButton)view.findViewById(R.id.item_frag_it_button);
		mItNumber = (TextView)view.findViewById(R.id.item_frag_it_number);
		mProductTag = (RelativeLayout)view.findViewById(R.id.item_frag_product_tag);

		mReplyTitle = (TextView)view.findViewById(R.id.reply_frag_title);
		mReplyTitlebarDivider = view.findViewById(R.id.reply_frag_titlebar_divider);
		mReplyListLayout = (FrameLayout)view.findViewById(R.id.reply_frag_list_layout);
		mReplyListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mReplyListEmptyView = (LinearLayout)view.findViewById(R.id.reply_frag_list_empty_view);
		mReplyInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mReplyInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);

		mProfileLayout = (LinearLayout)view.findViewById(R.id.item_frag_profile_layout);
		mProfileImage = (CircleImageView)view.findViewById(R.id.item_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.item_frag_nick_name);
	}


	private void setComponent(){
		mReplyTitlebarDivider.setVisibility(View.GONE);

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
				final int likeItNum = (Integer.parseInt(mItNumber.getText().toString()) + 1);
				mItNumber.setText(String.valueOf(likeItNum));

				LikeIt likeIt = new LikeIt(mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
				mAimHelper.add(mThisFragment, likeIt, new EntityCallback<LikeIt>() {

					@Override
					public void onCompleted(LikeIt entity) {
						mItem.setLikeItCount(likeItNum);
					}
				});
			}
		});

		mProductTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProductTagDialog productTagDialog = new ProductTagDialog(mThisFragment, mItem);
				productTagDialog.show(mThisFragment.getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mReplyInputSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Reply reply = new Reply(mReplyInputText.getText().toString(), mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
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


	private void setScrollView(){
		mScrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {

			@Override
			public void onScrollChanged(int scrollY, boolean firstScroll,
					boolean dragging) {
				mImage.scrollTo(0, scrollY/2);
			}
			@Override
			public void onDownMotionEvent() {
			}
			@Override
			public void onUpOrCancelMotionEvent(ScrollState scrollState) {
			}
		});
	}


	private void setReplyList(){
		mReplyListView.setHasFixedSize(true);

		mReplyListLayoutManager = new LinearLayoutManager(mActivity);
		mReplyListView.setLayoutManager(mReplyListLayoutManager);
		mReplyListView.setItemAnimator(new DefaultItemAnimator());

		mReplyList = new ArrayList<Reply>();
		mReplyListAdapter = new ReplyListAdapter(mActivity, mThisFragment, mItem, mReplyList);
		mReplyListAdapter.setReplyCallback(this);
		mReplyListView.setAdapter(mReplyListAdapter);
	}


	private void setText(){
		mContent.setText(mItem.getContent());
		mDate.setText(mItem.getCreateDateTime().getElapsedDateTime());
		mItNumber.setText(""+mItem.getLikeItCount());
		mNickName.setText(mItem.getWhoMade());
	}


	private void updateItemFrag(){
		mScrollView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				updateRecentReplyList(frag);
			}
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				mScrollView.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}
		});
	}


	private void updateRecentReplyList(final ItFragment frag) {
		mAimHelper.listRecent(mThisFragment, Reply.class, mItem.getId(), new ListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				mItem.setReplyCount(count);

				if(mItem.getReplyCount() > DISPLAY_REPLY_COUNT){
					mReplyListAdapter.setHasPrevious(true);
				} else {
					mReplyListAdapter.setHasPrevious(false);
				}
				resizeReplyListLayoutHeight(Math.min(mItem.getReplyCount(), DISPLAY_REPLY_COUNT+1));
				showReplyList(mItem.getReplyCount());
				setReplyTitle();

				mReplyList.clear();
				mReplyListAdapter.addAll(list);

				AsyncChainer.notifyNext(frag);
			}
		});
	}


	private void resizeReplyListLayoutHeight(int rowCount){
		int replyRowHeight = getResources().getDimensionPixelSize(R.dimen.reply_row_height);
		int replyPreviousRowHeight = getResources().getDimensionPixelSize(R.dimen.reply_row_previous_height);

		int height = 0;
		if(rowCount <= 0){
			height = replyRowHeight;
		} else if(!mReplyListAdapter.isHasPrevious()) {
			height = replyRowHeight * rowCount;
		} else {
			height = replyRowHeight * (rowCount - 1);
			height += replyPreviousRowHeight;
		}

		mReplyListLayout.getLayoutParams().height = height;
	}


	private void showReplyList(int replyCount){
		if(replyCount > 0){
			mReplyListEmptyView.setVisibility(View.GONE);
			mReplyListView.setVisibility(View.VISIBLE);
		} else {
			mReplyListEmptyView.setVisibility(View.VISIBLE);
			mReplyListView.setVisibility(View.GONE);
		}
	}


	private void setImageView(){
		Picasso.with(mImage.getContext())
		.load(BlobStorageHelper.getItemImgUrl(mItem.getId()))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(mImage);

		Picasso.with(mProfileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(mItem.getWhoMadeId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(mProfileImage);
	}


	private void submitReply(final Reply reply){
		resizeReplyListLayoutHeight(mItem.getReplyCount()+1);
		showReplyList(mItem.getReplyCount()+1);
		mReplyListAdapter.add(mReplyList.size(), reply);

		mAimHelper.add(mThisFragment, reply, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				mItem.setReplyCount(mItem.getReplyCount()+1);
				setReplyTitle();

				mReplyListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
	}


	private void deleteItem(final Item item){
		mApp.showProgressDialog(mActivity);
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(2);

				mAimHelper.delItem(mThisFragment, item, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag);
					}
				});

				mBlobStorageHelper.deleteBitmapAsync(mThisFragment, BlobStorageHelper.ITEM_IMAGE, item.getId(), new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag);
					}
				});
			}

		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				mApp.dismissProgressDialog();

				Intent intent = new Intent(mActivity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}


	private void setReplyTitle(){
		mReplyTitle.setText(getResources().getString(R.string.comments) + " " + mItem.getReplyCount());
	}
}
