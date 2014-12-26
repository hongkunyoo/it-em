package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.LikeIt;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.SquareImageView;
import com.squareup.picasso.Picasso;

public class ItemFragment extends ItFragment {

	private SquareImageView mImage;
	private TextView mContent;
	private TextView mDate;
	private TextView mItNumber;
	private Button mDelete;

	private LinearLayout mProfileLayout;
	private CircleImageView mProfileImage;
	private TextView mNickName;

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
		View view = inflater.inflate(R.layout.fragment_item, container, false);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		setText();
		setButton();
		setReplyFragment();
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
			int likeItNum = (Integer.parseInt(mItNumber.getText().toString().trim()) + 1);
			mItNumber.setText(String.valueOf(likeItNum));

			LikeIt likeIt = new LikeIt(mItem.getWhoMade(), mItem.getWhoMadeId(), mItem.getId());
			mAimHelper.add(mThisFragment, likeIt, null);
			break;
		}
		return super.onOptionsItemSelected(menu);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mItem.getWhoMade() + getResources().getString(R.string.of) 
				+ " " + getResources().getString(R.string.app_name));
	}


	private void findComponent(View view){
		mImage = (SquareImageView)view.findViewById(R.id.item_frag_image);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mItNumber = (TextView)view.findViewById(R.id.item_frag_it_number);
		mDelete = (Button)view.findViewById(R.id.item_frag_delete);
		mProfileLayout = (LinearLayout)view.findViewById(R.id.item_frag_profile_layout);
		mProfileImage = (CircleImageView)view.findViewById(R.id.item_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.item_frag_nick_name);
	}


	private void setText(){
		mContent.setText(mItem.getContent());
		mDate.setText(" " + mItem.getCreateDateTime().getElapsedDateTimeString());
		mItNumber.setText(mItem.getLikeItCount() + " ");
		mNickName.setText(mItem.getWhoMade());
	}


	private void setButton(){
		if(mItem.getWhoMadeId().equals(mObjectPrefHelper.get(ItUser.class).getId())){
			mDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});
		} else {
			mDelete.setVisibility(View.GONE);
		}

		mProfileLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ItUserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, mItem.getWhoMadeId());
				startActivity(intent);
			}
		});
	}


	private void setReplyFragment(){
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		ItFragment fragment = ReplyFragment.newInstance(mItem);
		transaction.replace(R.id.item_frag_reply_frag, fragment);
		transaction.commit();
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
}
