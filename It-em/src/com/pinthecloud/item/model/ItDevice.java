package com.pinthecloud.item.model;


public class ItDevice {

	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("whoMadeId")
	private String whoMadeId;
	@com.google.gson.annotations.SerializedName("mobileId")
	private String mobileId;
	@com.google.gson.annotations.SerializedName("registrationId")
	private String registrationId;
	@com.google.gson.annotations.SerializedName("mobileOs")
	private String mobileOs;

	public ItDevice() {
		super();
	}
	public ItDevice(String mobileId, String registrationId) {
		super();
		this.mobileId = mobileId;
		this.registrationId = registrationId;
		this.mobileOs = "ANDROID";
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWhoMadeId() {
		return whoMadeId;
	}
	public void setWhoMadeId(String whoMadeId) {
		this.whoMadeId = whoMadeId;
	}
	public String getMobileId() {
		return mobileId;
	}
	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getMobileOs() {
		return mobileOs;
	}
	public void setMobileOs(String mobileOs) {
		this.mobileOs = mobileOs;
	}
}
