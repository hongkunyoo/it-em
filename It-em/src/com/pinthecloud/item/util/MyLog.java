package com.pinthecloud.item.util;

import android.util.Log;

import com.pinthecloud.item.GlobalVariable;

public class MyLog {
	public static void log(Object... objs) {
		if(GlobalVariable.DEBUG_MODE){
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
}
