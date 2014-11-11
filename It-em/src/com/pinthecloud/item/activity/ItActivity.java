package com.pinthecloud.item.activity;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.analysis.UserHabitHelper;

public class ItActivity extends ActionBarActivity{

	protected ItApplication app;
	protected ItActivity thisActivity;
	protected UserHabitHelper userHabitHelper;


	public ItActivity(){
		app = ItApplication.getInstance();
		thisActivity = this;
		userHabitHelper = app.getUserHabitHelper();
	}


	@Override
	protected void onStart() {
		super.onStart();
		userHabitHelper.activityStart(thisActivity);
	}


	@Override
	protected void onStop() {
		super.onStop();
		userHabitHelper.activityStop(thisActivity);
	}


	public void Log(ItActivity activity, Object... params){
		if(GlobalVariable.DEBUG_MODE){
			Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Log.e("ERROR", "[ "+activity.getClass().getName() + " ]");
			for(Object str : params) {
				if (str == null) {
					Log.e("ERROR", "null");
					continue;
				}
				Log.e("ERROR", str.toString());
			}
			Log.e("ERROR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
	}
}
