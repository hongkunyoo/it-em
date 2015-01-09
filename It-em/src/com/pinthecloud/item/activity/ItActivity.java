package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.analysis.UserHabitHelper;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.interfaces.DialogCallback;

import de.greenrobot.event.EventBus;

public abstract class ItActivity extends ActionBarActivity {

	protected ItApplication mApp;
	protected ItActivity mThisActivity;
	protected UserHabitHelper mUserHabitHelper;
	protected GAHelper mGaHelper;

	public abstract Toolbar getToolbar();


	public ItActivity(){
		super();
		mApp = ItApplication.getInstance();
		mThisActivity = this;
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


	public void onEvent(ItException exception) {
		String title = null;
		String message = null;
		if(exception.getType().equals(ItException.TYPE.INTERNET_NOT_CONNECTED)){
			message = getResources().getString(R.string.internet_not_connected_message);
		} else {
			title = exception.getType().toString();
			message = exception.toString();
		}

		final ItAlertDialog exceptionDialog = new ItAlertDialog(title, message, null, null, false, new DialogCallback() {
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
