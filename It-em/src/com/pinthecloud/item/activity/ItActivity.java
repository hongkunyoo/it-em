package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.analysis.UserHabitHelper;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.DeviceHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;
import com.pinthecloud.item.helper.VersionHelper;
import com.pinthecloud.item.interfaces.DialogCallback;

import de.greenrobot.event.EventBus;

public abstract class ItActivity extends ActionBarActivity {

	protected ItApplication mApp;
	protected ItActivity mThisActivity;

	protected PrefHelper mPrefHelper;
	protected ObjectPrefHelper mObjectPrefHelper;
	protected AimHelper mAimHelper;
	protected UserHelper mUserHelper;
	protected VersionHelper mVersionHelper;
	protected DeviceHelper mDeviceHelper;
	protected BlobStorageHelper mBlobStorageHelper;

	protected UserHabitHelper mUserHabitHelper;
	protected GAHelper mGaHelper;
	
	public abstract View getToolbarLayout();


	public ItActivity(){
		super();
		mApp = ItApplication.getInstance();
		mThisActivity = this;

		mPrefHelper = mApp.getPrefHelper();
		mObjectPrefHelper = mApp.getObjectPrefHelper();
		mAimHelper = mApp.getAimHelper();
		mUserHelper = mApp.getUserHelper();
		mVersionHelper = mApp.getVersionHelper();
		mDeviceHelper = mApp.getDeviceHelper();
		mBlobStorageHelper = mApp.getBlobStorageHelper();

		mUserHabitHelper = mApp.getUserHabitHelper();
		mGaHelper = mApp.getGaHelper();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(mThisActivity);
	}


	@Override
	protected void onStart() {
		super.onStart();
		mUserHabitHelper.activityStart(mThisActivity);
		mGaHelper.reportActivityStart(mThisActivity);
	}


	@Override
	protected void onStop() {
		super.onStop();
		mUserHabitHelper.activityStop(mThisActivity);
		mGaHelper.reportActivityStop(mThisActivity);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(mThisActivity);
	}


	public void onEvent(ItException exception) {
		if(exception.getType().equals(ItException.TYPE.NETWORK_UNAVAILABLE)
				|| exception.getType().equals(ItException.TYPE.INTERNAL_ERROR)){

			String message = null;
			if(exception.getType().equals(ItException.TYPE.NETWORK_UNAVAILABLE)){
				message = getResources().getString(R.string.network_unavailable_message);
			} else if(exception.getType().equals(ItException.TYPE.INTERNAL_ERROR)){
				message = getResources().getString(R.string.error_message);
			}

			ItAlertDialog exceptionDialog = ItAlertDialog.newInstance(message, null, null, false);
			exceptionDialog.setCallback(new DialogCallback() {

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
			exceptionDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
		}
	}
}
