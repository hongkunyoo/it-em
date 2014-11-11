package com.pinthecloud.item.analysis;

import io.userhabit.service.Userhabit;
import android.app.Activity;

import com.pinthecloud.item.GlobalVariable;

public class UserHabitHelper {

	private final String USER_HABIT_KEY = "348f6a8907390f8c7090e999e57d68ea61ea2b72";


	public void activityStart(Activity activity){
		if(!GlobalVariable.DEBUG_MODE){
			Userhabit.activityStart(activity, USER_HABIT_KEY);
		}
	}


	public void activityStop(Activity activity){
		if(!GlobalVariable.DEBUG_MODE){
			Userhabit.activityStop(activity);
		}
	}
}
