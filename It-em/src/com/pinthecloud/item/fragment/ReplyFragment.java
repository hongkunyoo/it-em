package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.Reply;

public class ReplyFragment extends ItFragment {

	private ProgressBar mProgressBar;

	private ProgressBar mPreviousProgressBar;
	private TextView mPreviousText;

	private EditText mReplyText;
	private Button mSubmitButton;

	private RecyclerView mListView;
	private ReplyListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Reply> mReplyList;

	private Item mItem;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = mActivity.getIntent();
		mItem = intent.getParcelableExtra(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_reply, container, false);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();
		setList();
		loadReplyList();
		return view;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.reply_frag_progress_bar);
		mPreviousProgressBar = (ProgressBar)view.findViewById(R.id.reply_frag_previous_progress_bar);
		mPreviousText = (TextView)view.findViewById(R.id.reply_frag_previous_text);
		mSubmitButton = (Button)view.findViewById(R.id.reply_frag_submit);
		mListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mReplyText = (EditText)view.findViewById(R.id.reply_frag_reply_text);
	}


	private void setComponent(){
		mReplyText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					mSubmitButton.setEnabled(false);
				}else{
					mSubmitButton.setEnabled(true);
				}
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
		mPreviousText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadReplyList();
			}
		});

		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItUser me = mObjectPrefHelper.get(ItUser.class);
				Reply reply = new Reply(mReplyText.getText().toString(), me.getNickName(), me.getId(), mItem.getId());
				mReplyText.setText("");
				submitReply(reply);
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mReplyList = new ArrayList<Reply>();
		mListAdapter = new ReplyListAdapter(mActivity, mThisFragment, mReplyList);
		mListView.setAdapter(mListAdapter);
	}


	private void loadReplyList() {
		mAimHelper.list(mThisFragment, Reply.class, mItem.getId(), new ItListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mPreviousProgressBar.setVisibility(View.INVISIBLE);
				mListAdapter.addAll(0, list);
			}
		});
	}


	private void submitReply(final Reply reply){
		mListAdapter.add(mReplyList.size(), reply);
		mAimHelper.add(mThisFragment, reply, new ItEntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				int position = mReplyList.indexOf(reply);
				mListAdapter.replace(position, entity);
			}
		});
	}
}
