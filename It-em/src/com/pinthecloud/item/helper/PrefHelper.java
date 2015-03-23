package com.pinthecloud.item.helper;

import com.pinthecloud.item.ItConstant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PrefHelper {

	public static final String DEFAULT_STRING = "";
	public static final int DEFAULT_INT = 0;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final float DEFAULT_FLOAT = 0;

	private SharedPreferences mPref;
	public PrefHelper(Context context){
		this.mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void put(String key, Object obj) {
		if (obj instanceof Boolean){
			mPref.edit().putBoolean(key, (Boolean) obj).commit();
		} else if (obj instanceof String) {
			mPref.edit().putString(key, ((String) obj)).commit();
		} else if (obj instanceof Integer) {
			mPref.edit().putInt(key, (Integer) obj).commit();
		} else if (obj instanceof Float) {
			mPref.edit().putFloat(key, (Float) obj).commit();
		} else if (obj instanceof Double) {
			mPref.edit().putFloat(key, (Float) obj).commit();
		} else if (obj instanceof Character) {
			mPref.edit().putString(key, (Character.toString((Character)obj))).commit();
		} else if (obj instanceof Long) {
			mPref.edit().putLong(key, (Long) obj).commit();
		}
	}

	public Object get(String key, Class<?> clazz) {
		String className = clazz.getName();
		if (className.equals("java.lang.String") || className.equals("java.lang.Character") || className.equals("char")) {
			return mPref.getString(key, DEFAULT_STRING);
		} else if (className.equals("java.lang.Integer") || className.equals("int")) {
			return mPref.getInt(key, DEFAULT_INT);
		} else if (className.equals("java.lang.Float") || className.equals("java.lang.Double")
				|| className.equals("float") || className.equals("double")) {
			return mPref.getFloat(key, DEFAULT_FLOAT);
		} else if (className.equals("java.lang.Boolean") || className.equals("boolean")) {
			return mPref.getBoolean(key, DEFAULT_BOOLEAN);
		} else {
			return null;
		}
	}

	public String getString(String key){
		return mPref.getString(key, DEFAULT_STRING);
	}

	public int getInt(String key){
		return mPref.getInt(key, DEFAULT_INT);
	}

	public boolean getBoolean(String key){
		return mPref.getBoolean(key, DEFAULT_BOOLEAN);
	}

	public float getFloat(String key) {
		return mPref.getFloat(key, DEFAULT_FLOAT);
	}

	public void remove(String key){
		Editor editor = mPref.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public void clear(){
		int developMode = getInt(ItConstant.DEVELOP_MODE_KEY);
		Editor editor = mPref.edit();
		editor.clear();
		editor.commit();
		put(ItConstant.DEVELOP_MODE_KEY, developMode);
	}
}
