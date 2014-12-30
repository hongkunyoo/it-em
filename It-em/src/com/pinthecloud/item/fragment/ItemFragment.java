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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.helper.AimHelper;
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

	private ObservableScrollView mScrollView;
	private SquareImageView mImage;
	private TextView mContent;
	private TextView mDate;
	private TextView mItNumber;
	private Button mDelete;
	private ProgressBar mProgressBar;

	private TextView mReplyCount;
	private RelativeLayout mReplyListLayout;
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
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.item_it:
			int likeItNum = (Integer.parseInt(mItNumber.getText().toString()) + 1);
			mItNumber.setText(String.valueOf(likeItNum));

			LikeIt likeIt = new LikeIt(mItem.getWhoMade(), mItem.getWhoMadeId(), mItem.getId());
			mAimHelper.add(mThisFragment, likeIt, null);
			break;
		}
		return super.onOptionsItemSelected(menu);
	}


	@Override
	public void deleteReply(final Reply reply){
		AimHelper aimHelper = ItApplication.getInstance().getAimHelper();
		aimHelper.del(mThisFragment, reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				mItem.setReplyCount(mItem.getReplyCount()-1);
				mReplyCount.setText(""+mItem.getReplyCount());
				resizeReplyListLayoutHeight(mItem.getReplyCount());
				showReplyList(mItem.getReplyCount());

				mReplyListAdapter.remove(reply);
			}
		});
	}


	private void findComponent(View view){
		mScrollView = (ObservableScrollView)view.findViewById(R.id.item_frag_scroll_layout);
		mImage = (SquareImageView)view.findViewById(R.id.item_frag_image);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mItNumber = (TextView)view.findViewById(R.id.item_frag_it_number);
		mDelete = (Button)view.findViewById(R.id.item_frag_delete);

		mReplyCount = (TextView)view.findViewById(R.id.item_frag_reply_count);
		mReplyListLayout = (RelativeLayout)view.findViewById(R.id.item_frag_reply_list_layout);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mReplyListView = (RecyclerView)view.findViewById(R.id.item_frag_reply_list);
		mReplyListEmptyView = (LinearLayout)view.findViewById(R.id.item_frag_reply_list_empty_view);
		mReplyInputText = (EditText)view.findViewById(R.id.custom_inputbar_text);
		mReplyInputSubmit = (Button)view.findViewById(R.id.custom_inputbar_submit);

		mProfileLayout = (LinearLayout)view.findViewById(R.id.item_frag_profile_layout);
		mProfileImage = (CircleImageView)view.findViewById(R.id.item_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.item_frag_nick_name);
	}


	private void setComponent(){
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
		if(mItem.getWhoMadeId().equals(mMyItUser.getId())){
			mDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});
		} else {
			mDelete.setVisibility(View.GONE);
		}

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
		mReplyListAdapter = new ReplyListAdapter(mActivity, mThisFragment, mMyItUser, mItem, mReplyList);
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
		mAimHelper.list(mThisFragment, Reply.class, mItem.getId(), new ListCallback<Reply>() {

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
				mReplyCount.setText(""+mItem.getReplyCount());

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
				mReplyCount.setText(""+mItem.getReplyCount());

				mReplyListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
	}
}
