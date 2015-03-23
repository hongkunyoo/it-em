package com.pinthecloud.item.util;

import android.util.Log;

import com.google.gson.Gson;

public class ItLog {

	public static void log(Object... objs) {
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

	public static void logObject(Object obj) {
		log(new Gson().toJson(obj));
	}
}
