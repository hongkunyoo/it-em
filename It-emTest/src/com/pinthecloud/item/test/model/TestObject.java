package com.pinthecloud.item.test.model;

import com.google.gson.Gson;

public class TestObject {
	private int testInt;
	private String testString;
	private boolean testBoolean;
	private float testFloat;
	
	public int getTestInt() {
		return testInt;
	}
	public void setTestInt(int testInt) {
		this.testInt = testInt;
	}
	public String getTestString() {
		return testString;
	}
	public void setTestString(String testString) {
		this.testString = testString;
	}
	public boolean isTestBoolean() {
		return testBoolean;
	}
	public void setTestBoolean(boolean testBoolean) {
		this.testBoolean = testBoolean;
	}
	public float getTestFloat() {
		return testFloat;
	}
	public void setTestFloat(float testFloat) {
		this.testFloat = testFloat;
	}
	public String toString() {
		return new Gson().toJson(this);
	}
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof TestObject)) return false;
		TestObject other = (TestObject)obj;
		if (other.testInt == this.testInt && this.testBoolean == other.testBoolean
				&& this.testFloat == other.testFloat && this.testString.equals(other.testString)) return true;
		return false;
	}
}
