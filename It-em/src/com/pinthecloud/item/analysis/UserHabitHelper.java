package com.pinthecloud.item.analysis;

import io.userhabit.service.Userhabit;
import android.app.Activity;

import com.pinthecloud.item.ItApplication;

public class UserHabitHelper {

	private final String USER_HABIT_KEY = "348f6a8907390f8c7090e999e57d68ea61ea2b72";


	public void activityStart(Activity activity){
		if(!ItApplication.isDebugging()){
			Userhabit.activityStart(activity, USER_HABIT_KEY);
		}
	}


	public void activityStop(Activity activity){
		if(!ItApplication.isDebugging()){
			Userhabit.activityStop(activity);
		}
	}
}
