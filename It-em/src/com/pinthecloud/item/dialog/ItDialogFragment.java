package com.pinthecloud.item.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;

public class ItDialogFragment extends DialogFragment {

	public static final String INTENT_KEY = "DIALOG_INTENT_KEY";

	protected ItApplication mApp;
	protected ItActivity mActivity;
	protected ItDialogFragment mThisFragment;

	protected PrefHelper mPrefHelper;
	protected ObjectPrefHelper mObjectPrefHelper;
	protected AimHelper mAimHelper;
	protected UserHelper mUserHelper;
	protected BlobStorageHelper blobStorageHelper;

	private boolean isShowing = false;


	public ItDialogFragment() {
		super();
		mApp = ItApplication.getInstance();
		mThisFragment = this;

		mPrefHelper = mApp.getPrefHelper();
		mObjectPrefHelper = mApp.getObjectPrefHelper();
		mAimHelper = mApp.getAimHelper();
		mUserHelper = mApp.getUserHelper();
		blobStorageHelper = mApp.getBlobStorageHelper();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (ItActivity) getActivity();
	}


	@Override
	public void show(FragmentManager manager, String tag) {
		if(isShowing){
			return;
		}else{
			super.show(manager, tag);
			isShowing = true;
		}
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
		isShowing = false;
		super.onDismiss(dialog);
	}


	public boolean isShowing(){
		return isShowing;
	}
}
