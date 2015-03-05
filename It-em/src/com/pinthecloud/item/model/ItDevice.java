package com.pinthecloud.item.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class ItDevice implements Parcelable {

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
	public void readDeviceInfo(ItDevice deviceInfo) {
		this.setId(deviceInfo.getId());
		this.setWhoMadeId(deviceInfo.getWhoMadeId());
		this.setMobileId(deviceInfo.getMobileId());
		this.setRegistrationId(deviceInfo.getRegistrationId());
		this.setMobileOs(deviceInfo.getMobileOs());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<ItDevice> CREATOR = new Creator<ItDevice>(){
		public ItDevice createFromParcel(Parcel in){
			return new ItDevice(in);
		}
		public ItDevice[] newArray(int size){
			return new ItDevice[size]; 
		}
	};

	public ItDevice(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readDeviceInfo(new Gson().fromJson(in.readString(), ItDevice.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
