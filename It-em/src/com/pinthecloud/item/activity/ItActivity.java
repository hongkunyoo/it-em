package com.pinthecloud.item.activity;

import android.support.v7.app.ActionBarActivity;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.analysis.UserHabitHelper;

public class ItActivity extends ActionBarActivity{

	protected ItApplication app;
	protected ItActivity thisActivity;
	protected UserHabitHelper userHabitHelper;
	protected GAHelper gaHelper;


	public ItActivity(){
		app = ItApplication.getInstance();
		thisActivity = this;
		userHabitHelper = app.getUserHabitHelper();
		gaHelper = app.getGaHelper();
	}


	@Override
	protected void onStart() {
		super.onStart();
		userHabitHelper.activityStart(thisActivity);
		gaHelper.reportActivityStart(thisActivity);
	}


	@Override
	protected void onStop() {
		super.onStop();
		userHabitHelper.activityStop(thisActivity);
		gaHelper.reportActivityStop(thisActivity);
	}
}
