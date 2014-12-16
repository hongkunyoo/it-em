package com.pinthecloud.item.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;
import com.pinthecloud.item.interfaces.ItDialogCallback;

public class ItFragment extends Fragment implements ExceptionManager.Handler {

	protected ItApplication mApp;
	protected ItActivity mActivity;
	protected ItFragment mThisFragment;

	protected PrefHelper mPrefHelper;
	protected ObjectPrefHelper mObjectPrefHelper;
	protected AimHelper mAimHelper;
	protected UserHelper mUserHelper;
	protected BlobStorageHelper blobStorageHelper;

	public ItFragment(){
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
		ExceptionManager.setHandler(mThisFragment);
	}


	@Override
	public void handleException(final ItException ex) {
		String title = null;
		String message = null;
		if(ex.getType().equals(ItException.TYPE.INTERNET_NOT_CONNECTED)){
			message = getResources().getString(R.string.internet_not_connected_message);
		} else {
			title = ex.getType().toString();
			message = ex.toString();
		}

		ItAlertDialog exceptionDialog = new ItAlertDialog(title, message, null, null, false, new ItDialogCallback() {
			@Override
			public void doPositiveThing(Bundle bundle) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Do nothing
			}
		}); 
		exceptionDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
	}
}
