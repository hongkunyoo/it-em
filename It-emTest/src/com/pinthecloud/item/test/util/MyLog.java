package com.pinthecloud.item.test.util;

import android.util.Log;

public class MyLog {
	public static void log(Object... objs) {
		Log.e("ERROR",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (Object obj : objs) {
			if (obj == null) {
				Log.e("ERROR" ,"null");
			} else {
				Log.e("ERROR" ,obj.toString());
			}
		}
		Log.e("ERROR" ,"\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}
}
