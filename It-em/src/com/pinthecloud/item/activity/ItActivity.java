package com.pinthecloud.item.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.analysis.UserHabitHelper;

public abstract class ItActivity extends ActionBarActivity {

	protected ItApplication mApp;
	protected ItActivity mThisActivity;
	protected UserHabitHelper mUserHabitHelper;
	protected GAHelper mGaHelper;


	public ItActivity(){
		super();
		mApp = ItApplication.getInstance();
		mThisActivity = this;
		mUserHabitHelper = mApp.getUserHabitHelper();
		mGaHelper = mApp.getGaHelper();
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
	
	public abstract Toolbar getToolbar();
}
