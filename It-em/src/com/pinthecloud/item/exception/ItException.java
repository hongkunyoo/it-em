package com.pinthecloud.item.exception;

import com.google.gson.Gson;
import com.pinthecloud.item.fragment.ItFragment;

public class ItException extends RuntimeException {

	private static final long serialVersionUID = -5944663372661859514L;

	private ItException.TYPE type;
	private ItFragment from;
	private String methodName;
	private Object parameter;

	public ItException(String string) {
		super(string);
	}

	public ItException(TYPE type) {
		this.type = type;
		this.from = null;
	}

	public ItException(ItFragment from, String methodName, TYPE type) {
		this.from = from;
		this.type = type;
		this.methodName = methodName;
	}

	public ItException(ItFragment from, String methodName, TYPE type, Object parameter) {
		this.from = from;
		this.type = type;
		this.methodName = methodName;
		this.parameter = parameter;
	}

	public TYPE getType() {
		return type;
	}

	public ItFragment fromWho() {
		return from;
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

	public enum TYPE {
		INTERNET_NOT_CONNECTED,
		SERVER_ERROR,
		BLOB_STORAGE_ERROR,
		NO_SUCH_INSTANCE,
		FORMATE_ERROR
	}
}
