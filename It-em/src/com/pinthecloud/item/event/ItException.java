package com.pinthecloud.item.event;

import com.google.gson.Gson;


public class ItException extends RuntimeException {

	private static final long serialVersionUID = -5944663372661859514L;

	private ItException.TYPE type;
	private String methodName;
	private Object parameter;

	public ItException(String string) {
		super(string);
	}

	public ItException(TYPE type) {
		this.type = type;
	}

	public ItException(String methodName, TYPE type) {
		this.type = type;
		this.methodName = methodName;
	}

	public ItException(String methodName, TYPE type, Object parameter) {
		this.type = type;
		this.methodName = methodName;
		this.parameter = parameter;
	}

	public TYPE getType() {
		return type;
	}

	public String getMethodName() {
		return methodName;
	}

	public Object getParameter() {
		return parameter;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	// Client Exception Type
	public enum TYPE {
		NETWORK_UNAVAILABLE,
		SERVER_ERROR,
		BLOB_STORAGE_ERROR,
		INTERNAL_ERROR,
		GCM_REGISTRATION_FAIL,
		KAKAO_LOGIN_FAIL
	}

	// Azure Mobile Service Exception Code
	public static final String ITEM_NOT_FOUND = "ItemNotFound";
}
