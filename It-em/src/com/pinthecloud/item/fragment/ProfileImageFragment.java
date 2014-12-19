package com.pinthecloud.item.fragment;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.view.SquareImageView;
import com.squareup.picasso.Picasso;

public class ProfileImageFragment extends ItFragment {

	private String mItUserId;
	private SquareImageView mProfileImage; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItUserId = mActivity.getIntent().getStringExtra(ItUser.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
		mProfileImage = (SquareImageView) view.findViewById(R.id.profile_image_frag_view);
		new PhotoViewAttacher(mProfileImage);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		Picasso.with(mProfileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(mItUserId))
		.placeholder(null)
		.fit()
		.into(mProfileImage);
	}


	@Override
	public void onStop() {
		mProfileImage.setImageBitmap(null);
		super.onStop();
	}
}
