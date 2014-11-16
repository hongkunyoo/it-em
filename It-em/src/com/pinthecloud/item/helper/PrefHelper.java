package com.pinthecloud.item.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.exception.ItException;

public class PrefHelper {

	public static final String DEFAULT_STRING = "DEFAULT_STRING";
	public static final int DEFAULT_INT = 0;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final float DEFAULT_FLOAT = 0;

	private SharedPreferences pref;

	private static PrefHelper prefHelper = null;
	public static PrefHelper getInstance() {
		if (prefHelper == null) {
			prefHelper = new PrefHelper(ItApplication.getInstance());
		}
		return prefHelper;
	}

	private PrefHelper(Context context){
		this.pref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void put(String key, Object obj) {
		if (obj instanceof Boolean)
			pref.edit().putBoolean(key, (Boolean) obj).commit();
		else if (obj instanceof String)
			pref.edit().putString(key, ((String) obj)).commit();
		else if (obj instanceof Integer)
			pref.edit().putInt(key, (Integer) obj).commit();
		else if (obj instanceof Float)
			pref.edit().putFloat(key, (Float) obj).commit();
		else if (obj instanceof Long)
			pref.edit().putLong(key, (Long) obj).commit();
		else {
			Log.e("ERROR", "No such instance" + obj.getClass());
			throw new ItException(ItException.TYPE.NO_SUCH_INSTANCE);
		}

	}
	public String getString(String key){
		return pref.getString(key, DEFAULT_STRING);
	}

	public int getInt(String key){
		return pref.getInt(key, DEFAULT_INT);
	}

	public boolean getBoolean(String key){
		return pref.getBoolean(key, DEFAULT_BOOLEAN);
	}

	public float getFloat(String key) {
		return pref.getFloat(key, DEFAULT_FLOAT);
	}

	public boolean remove(String key){
		Editor editor = pref.edit();
		editor.remove(key);
		return editor.commit();
	}
}
