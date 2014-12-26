package com.pinthecloud.item.fragment;

import java.util.ArrayList;
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
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.Reply;

public class ReplyFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private EditText mReplyText;
	private Button mSubmitButton;

	private RecyclerView mListView;
	private ReplyListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Reply> mReplyList;

	private ItUser mMyItUser;
	private Item mItem;


	public static ReplyFragment newInstance(Item item) {
		ReplyFragment fragment = new ReplyFragment();
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
		actionBar.setTitle(getResources().getString(R.string.reply) + " " + mItem.getReplyCount());
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.reply_frag_progress_bar);
		mSubmitButton = (Button)view.findViewById(R.id.reply_frag_submit);
		mListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mReplyText = (EditText)view.findViewById(R.id.reply_frag_reply_text);
	}


	private void setComponent(){
		mReplyText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String reply = s.toString().trim();
				mSubmitButton.setEnabled(reply.length() > 0);
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
		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Reply reply = new Reply(mReplyText.getText().toString(), mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
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
		mListAdapter = new ReplyListAdapter(mActivity, mThisFragment, mMyItUser, mReplyList);
		mListView.setAdapter(mListAdapter);
	}


	private void loadReplyList() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);

		mAimHelper.list(mThisFragment, Reply.class, mItem.getId(), new ListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				mListAdapter.addAll(0, list);
			}
		});
	}


	private void submitReply(final Reply reply){
		mListAdapter.add(mReplyList.size(), reply);
		mListView.smoothScrollToPosition(mReplyList.indexOf(reply));
		mAimHelper.add(mThisFragment, reply, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				mListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
	}
}
