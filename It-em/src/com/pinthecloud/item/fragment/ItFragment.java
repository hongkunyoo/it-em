package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.DeviceHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;
import com.pinthecloud.item.helper.VersionHelper;

public class ItFragment extends Fragment {

	protected ItApplication mApp;
	protected ItActivity mActivity;
	protected ItFragment mThisFragment;
	
	protected PrefHelper mPrefHelper;
	protected ObjectPrefHelper mObjectPrefHelper;
	protected AimHelper mAimHelper;
	protected UserHelper mUserHelper;
	protected VersionHelper mVersionHelper;
	protected DeviceHelper mDeviceHelper;
	protected BlobStorageHelper mBlobStorageHelper;

	protected GAHelper mGaHelper;

	
	public ItFragment(){
		super();
		mApp = ItApplication.getInstance();
		mThisFragment = this;

		mPrefHelper = mApp.getPrefHelper();
		mObjectPrefHelper = mApp.getObjectPrefHelper();
		mAimHelper = mApp.getAimHelper();
		mUserHelper = mApp.getUserHelper();
		mVersionHelper = mApp.getVersionHelper();
		mDeviceHelper = mApp.getDeviceHelper();
		mBlobStorageHelper = mApp.getBlobStorageHelper();
		
		mGaHelper = mApp.getGaHelper();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (ItActivity) getActivity();
	}
}
