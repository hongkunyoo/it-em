package com.pinthecloud.item.fragment;

import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.common.collect.Lists;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.model.Reply;

public class ReplyFragment extends ItFragment {

	private ProgressBar mProgressBar;

	private LinearLayout mPreviousLayout;
	private ProgressBar mPreviousProgressBar;

	private EditText mReplyText;
	private Button mSubmitButton;

	private RecyclerView mListView;
	private ReplyListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Reply> mReplyList;


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
		updateList();
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
		mPreviousLayout = (LinearLayout)view.findViewById(R.id.reply_frag_previous_layout);
		mPreviousProgressBar = (ProgressBar)view.findViewById(R.id.reply_frag_previous_progress_bar);
		mSubmitButton = (Button)view.findViewById(R.id.reply_frag_submit);
		mListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
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
		mPreviousLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadPreviousReplyList();
			}
		});

		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String reply = mReplyText.getText().toString();
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mReplyList = Lists.newArrayList();
		mListAdapter = new ReplyListAdapter(mActivity, mReplyList);
		mListView.setAdapter(mListAdapter);
	}


	private void updateList() {
		mProgressBar.setVisibility(View.GONE);
		mListAdapter.notifyDataSetChanged();
	}


	private void loadPreviousReplyList() {
		mPreviousProgressBar.setVisibility(View.VISIBLE);

		mPreviousProgressBar.setVisibility(View.GONE);
		mListAdapter.notifyDataSetChanged();
	}


	private void submitReply(Reply reply){
	}
}
