package com.pinthecloud.item.model;

public class AppVersion {

	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("version")
	private float version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getVersion() {
		return version;
	}
	public void setVersion(float version) {
		this.version = version;
	}
}
