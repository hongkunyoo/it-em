package com.pinthecloud.item.util;

import android.util.Log;

import com.google.gson.Gson;
import com.pinthecloud.item.GlobalVariable;

public class ItLog {

	public static void log(Object... objs) {
		if(GlobalVariable.DEBUG_MODE){
			Log.e("Log",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			for (Object obj : objs) {
				if (obj == null) {
					Log.e("ERROR" ,"null");
				} else {
					Log.e("ERROR" ,obj.toString());
				}
			}
			Log.e("Log","<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
	}

	public static void logObject(Object obj) {
		if(GlobalVariable.DEBUG_MODE){
			log(new Gson().toJson(obj));
		}
	}
}
