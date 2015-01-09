package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;

public class ItFragment extends Fragment {

	protected ItApplication mApp;
	protected ItActivity mActivity;
	protected ItFragment mThisFragment;

	protected PrefHelper mPrefHelper;
	protected ObjectPrefHelper mObjectPrefHelper;
	protected AimHelper mAimHelper;
	protected UserHelper mUserHelper;
	protected BlobStorageHelper mBlobStorageHelper;

	public ItFragment(){
		super();
		mApp = ItApplication.getInstance();
		mThisFragment = this;

		mPrefHelper = mApp.getPrefHelper();
		mObjectPrefHelper = mApp.getObjectPrefHelper();
		mAimHelper = mApp.getAimHelper();
		mUserHelper = mApp.getUserHelper();
		mBlobStorageHelper = mApp.getBlobStorageHelper();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (ItActivity) getActivity();
	}
}
