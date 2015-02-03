package com.pinthecloud.item.model;


public class CrashLog {

	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("log")
	private String log;
	@com.google.gson.annotations.SerializedName("androidVersion")
	private String androidVersion;
	@com.google.gson.annotations.SerializedName("versionCode")
	private String versionCode;
	@com.google.gson.annotations.SerializedName("versionName")
	private String versionName;
	@com.google.gson.annotations.SerializedName("brand")
	private String brand;
	@com.google.gson.annotations.SerializedName("model")
	private String model;
	@com.google.gson.annotations.SerializedName("startDate")
	private String startDate;
	
	public CrashLog() {
		super();
	}
	public CrashLog(String log, String androidVersion, String versionCode,
			String versionName, String brand, String model, String startDate) {
		super();
		this.log = log;
		this.androidVersion = androidVersion;
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.brand = brand;
		this.model = model;
		this.startDate = startDate;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getAndroidVersion() {
		return androidVersion;
	}
	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}
