package com.pinthecloud.item.dialog;

import java.util.ArrayList;
import java.util.List;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.interfaces.ReplyCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.util.ItLog;

public class ReplyDialog extends ItDialogFragment implements ReplyCallback {

	private TextView mTitle;

	private View mListLayout;
	private ProgressBar mProgressBar;
	private TextView mListEmptyView;
	private RecyclerView mListView;
	private ReplyListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Reply> mReplyList;

	private EditText mInputText;
	private Button mInputSubmit;

	private Item mItem;
	private ItUser mMyItUser;


	public static ReplyDialog newInstance(Item item) {
		ReplyDialog dialog = new ReplyDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		dialog.setArguments(bundle);
		return dialog;
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
		View view = inflater.inflate(R.layout.dialog_reply, container, false);

		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		findComponent(view);
		setComponent();
		setButton();
		setList();
		updateList();

		return view;
	}


	@Override
	public void deleteReply(final Reply reply){
		mApp.getAimHelper().del(reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				mItem.setReplyCount(mItem.getReplyCount()-1);
				setTitle(mItem.getReplyCount());
				showReplyList(mItem.getReplyCount());

				mListAdapter.remove(reply);
			}
		});
	}


	private void findComponent(View view){
		mTitle = (TextView)view.findViewById(R.id.reply_frag_title);
		mListLayout = view.findViewById(R.id.reply_frag_list_layout);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mListEmptyView = (TextView)view.findViewById(R.id.reply_frag_list_empty_view);
		mListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);
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
		mListAdapter = new ReplyListAdapter(mActivity, mItem, mReplyList);
		mListAdapter.setReplyCallback(this);
		mListView.setAdapter(mListAdapter);
	}


	private void updateList() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListLayout.setVisibility(View.INVISIBLE);

		mAimHelper.list(Reply.class, mItem.getId(), new ListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				if(isAdded()){
					mProgressBar.setVisibility(View.GONE);
					mListLayout.setVisibility(View.VISIBLE);

					ItLog.log("count : " + count);
					mItem.setReplyCount(count);
					showReplyList(mItem.getReplyCount());
					setTitle(mItem.getReplyCount());

					mReplyList.clear();
					mListAdapter.addAll(list);
				}
			}
		});
	}


	private void showReplyList(int replyCount){
		if(replyCount > 0){
			mListEmptyView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		} else {
			mListEmptyView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}


	private void submitReply(final Reply reply){
		mListAdapter.add(mReplyList.size(), reply);
		showReplyList(mItem.getReplyCount()+1);

		mAimHelper.add(reply, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply entity) {
				mItem.setReplyCount(mItem.getReplyCount()+1);
				setTitle(mItem.getReplyCount());

				mListAdapter.replace(mReplyList.indexOf(reply), entity);
			}
		});
	}


	private void setTitle(int replyCount){
		String title = getResources().getString(R.string.comments);
		if(replyCount != 0){
			title = title + " " + replyCount;
		}
		mTitle.setText(title);
	}
}
