package com.pinthecloud.item.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pinthecloud.item.exception.ItException;

public class PrefHelper {

	// Default Vaule
	public static final String DEFAULT_STRING = "DEFAULT_STRING";
	public static final int DEFAULT_INT = 0;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final float DEFAULT_FLOAT = 0;
	
	// Key
	public static final String MAIN_EXIT_TAB = "MAIN_EXIT_TAB";

	
	private SharedPreferences pref;
	public PrefHelper(Context context){
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
		else if (obj instanceof Double)
			pref.edit().putFloat(key, (Float) obj).commit();
		else if (obj instanceof Character)
			pref.edit().putString(key, (Character.toString((Character)obj))).commit();
		else if (obj instanceof Long)
			pref.edit().putLong(key, (Long) obj).commit();
		else {
//			Log.e("ERROR", "No such instance - key : " + key + " clasName : " +obj.getClass());
//			throw new ItException(ItException.TYPE.NO_SUCH_INSTANCE);
		}
	}
	
	public Object get(String key, Class<?> clazz) {
		String className = clazz.getName();
		if (className.equals("java.lang.String") || className.equals("java.lang.Character")
				|| className.equals("char")) {
			return pref.getString(key, DEFAULT_STRING);
		} else if (className.equals("java.lang.Integer") || className.equals("int")) {
			return pref.getInt(key, DEFAULT_INT);
		} else if (className.equals("java.lang.Float") || className.equals("java.lang.Double")
				|| className.equals("float") || className.equals("double")) {
			return pref.getFloat(key, DEFAULT_FLOAT);
		} else if (className.equals("java.lang.Boolean") || className.equals("boolean")) {
			return pref.getBoolean(key, DEFAULT_BOOLEAN);
		} else {
			Log.e("ERROR", "No such primative - key : " + key + " / class : " +clazz);
			throw new ItException(ItException.TYPE.NO_SUCH_PRIMATIVE);
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
