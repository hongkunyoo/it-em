package com.pinthecloud.item.fragment;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileImageFragment extends ItFragment {

	private ImageView mProfileImage;
	private PhotoViewAttacher mAttacher;

	private String mItUserId;
	private Bitmap mProfileImageBitmap;


	public static ItFragment newInstance(String itUserId, Bitmap profileImage) {
		ItFragment fragment = new ProfileImageFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ItUser.INTENT_KEY, itUserId);
		bundle.putParcelable(ItUser.INTENT_KEY_IMAGE, profileImage);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItUserId = getArguments().getString(ItUser.INTENT_KEY);
		mProfileImageBitmap = getArguments().getParcelable(ItUser.INTENT_KEY_IMAGE);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
		mProfileImage = (ImageView) view.findViewById(R.id.profile_image_frag_profile_image);
		mAttacher = new PhotoViewAttacher(mProfileImage);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		Picasso.with(mProfileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(mItUserId))
		.placeholder(new BitmapDrawable(getResources(), mProfileImageBitmap))
		.fit()
		.into(mProfileImage, new Callback(){

			@Override
			public void onSuccess() {
				mAttacher.update();
			}
			@Override
			public void onError() {
			}
		});
	}


	@Override
	public void onStop() {
		super.onStop();
		mProfileImage.setImageBitmap(null);
	}
}
