package com.pinthecloud.item.dialog;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.LikeListAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.ViewUtil;

public class LikeDialog extends ItDialogFragment {

	private ProgressBar mProgressBar;
	private RecyclerView mListView;
	private LikeListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<LikeIt> mLikeList;

	private Item mItem;


	public static LikeDialog newInstance(Item item) {
		LikeDialog dialog = new LikeDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItem = getArguments().getParcelable(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_like, container, false);

		mGaHelper.sendScreen(mThisFragment);
		findComponent(view);
		setList();
		updateList();

		return view;
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mListView = (RecyclerView)view.findViewById(R.id.like_frag_list);
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mLikeList = new ArrayList<LikeIt>();
		mListAdapter = new LikeListAdapter(mActivity, mLikeList);
		mListView.setAdapter(mListAdapter);
	}


	private void updateList() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);

		mAimHelper.list(LikeIt.class, mItem.getId(), new ListCallback<LikeIt>() {

			@Override
			public void onCompleted(List<LikeIt> list, int count) {
				if(!isAdded()){
					return;
				}

				mProgressBar.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				mLikeList.clear();
				mListAdapter.addAll(list);

				mItem.setLikeItCount(count);
				ViewUtil.setListHeightBasedOnChildren(mListView, count);
			}
		});
	}
}
