package com.pinthecloud.item.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;

import com.pinthecloud.item.ItApplication;

public class ObjectPrefHelper {

	private PrefHelper pref;

	public ObjectPrefHelper(Context context) {
		pref = ItApplication.getInstance().getPrefHelper();
		if (pref == null)
			pref = new PrefHelper(context);
	}

	public ObjectPrefHelper() {
		pref = ItApplication.getInstance().getPrefHelper();
	}

	public <E> void put(E obj) {
		Class<?> clazz = obj.getClass();
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (!isGetter(method)) continue;

			pref.put(removePrefix(method.getName()), invokeGetMethod(obj, method));
		}
	}

	public <E> E get(E obj) {
		Class<?> clazz = obj.getClass();
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (!isSetter(method)) continue;

			Class<?> paramClazz = method.getParameterTypes()[0];
			invokeSetMethod(obj, method, pref.get(removePrefix(method.getName()), paramClazz));
		}

		return obj;
	}

	private Object invokeGetMethod(Object obj, Method method) {
		try {
			return method.invoke(obj);
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}

	private void invokeSetMethod(Object obj, Method method, Object arg) {

		try {
			method.invoke(obj, arg);
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		}
	}

	private boolean isGetter(Method method) {
		if (method.getName().equals("getClass")) return false;
		if (!(method.getName().startsWith("get") 
				|| method.getName().startsWith("is")))
			return false;
		if (method.getParameterTypes().length != 0)
			return false;
		if (void.class.equals(method.getReturnType()))
			return false;
		return true;
	}

	private boolean isSetter(Method method) {
		if (!method.getName().startsWith("set"))
			return false;
		if (method.getParameterTypes().length != 1)
			return false;
		return true;
	}

	private String removePrefix(String name) {
		return name.split("(is)|(get)|(set)")[1];
	}
}
