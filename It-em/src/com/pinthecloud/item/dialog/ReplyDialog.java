package com.pinthecloud.item.dialog;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.Reply;

public class ReplyDialog extends ItDialogFragment {

	private ItFragment mFrag;
	private Item mItem;
	private ItUser mMyItUser;

	private Toolbar mToolbar;

	private ProgressBar mProgressBar;
	private RecyclerView mListView;
	private ReplyListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Reply> mReplyList;

	private EditText mInputText;
	private Button mInputSubmit;


	public ReplyDialog(ItFragment frag, Item item) {
		super();
		this.mFrag = frag;
		this.mItem = item;
		setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_reply, container, false);

		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setHasOptionsMenu(true);

		findComponent(view);
		setToolbar();
		setComponent();
		setButton();
		setList();

		if(mItem.getReplyCount() > 0){
			updateList();
		}

		return view;
	}


	private void findComponent(View view){
		mToolbar = (Toolbar)view.findViewById(R.id.toolbar_light);
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		mListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);
	}


	private void setToolbar(){
		mToolbar.setTitle(getResources().getString(R.string.reply) + " " + mItem.getReplyCount());
	}


	private void setComponent(){
		mInputText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String reply = s.toString().trim();
				mInputSubmit.setEnabled(reply.length() > 0);
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
		mInputSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Reply reply = new Reply(mInputText.getText().toString(), mMyItUser.getNickName(), mMyItUser.getId(), mItem.getId());
				mInputText.setText("");
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
		mListAdapter = new ReplyListAdapter(mActivity, mFrag, mMyItUser, mItem, mReplyList);
		mListView.setAdapter(mListAdapter);
	}


	private void updateList() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);

		mAimHelper.list(mFrag, Reply.class, mItem.getId(), new ListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				mReplyList.clear();
				mListAdapter.addAll(list);
			}
		});
	}


	private void submitReply(final Reply reply){
		mListAdapter.add(mReplyList.size(), reply);
		mListView.smoothScrollToPosition(mReplyList.indexOf(reply));
		mAimHelper.add(mFrag, reply, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				mListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
	}
}
