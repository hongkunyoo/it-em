package com.pinthecloud.item.analysis;

import io.userhabit.service.Userhabit;
import android.support.v4.app.Fragment;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.ItActivity;

public class UserHabitHelper {

	private final String USER_HABIT_KEY = "348f6a8907390f8c7090e999e57d68ea61ea2b72";


	public void activityStart(ItActivity activity){
		if(!ItApplication.isDebugging()){
			Userhabit.activityStart(activity, USER_HABIT_KEY);
		}
	}


	public void activityStop(ItActivity activity){
		if(!ItApplication.isDebugging()){
			Userhabit.activityStop(activity);
		}
	}
	
	
	public void setScreen(Fragment frag){
		if(!ItApplication.isDebugging()){
			Userhabit.setScreen(frag.getClass().getSimpleName());
		}
	}
}
