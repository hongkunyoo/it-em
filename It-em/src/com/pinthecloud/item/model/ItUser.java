package com.pinthecloud.item.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.helper.PrefHelper;

public class ItUser implements Parcelable {

	public static final String INTENT_KEY = "IT_USER_INTENT_KEY";

	public static enum PLATFORM {
		FACEBOOK,
		KAKAO
	}
	
	public static enum TYPE {
		VIEWER("VIEWER"),
		SELLER("SELLER"),
		PRO("PRO");

		private final String value;

		private TYPE(final String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return this.value;
		}
	}

	@com.google.gson.annotations.SerializedName("id")
	private String id;
	
	@com.google.gson.annotations.SerializedName("itUserId")
	private String itUserId;
	@com.google.gson.annotations.SerializedName("platform")
	private String platform;
	@com.google.gson.annotations.SerializedName("nickName")
	private String nickName;
	@com.google.gson.annotations.SerializedName("type")
	private String type;
	@com.google.gson.annotations.SerializedName("registrationId")
	private String registrationId;
	@com.google.gson.annotations.SerializedName("mobileId")
	private String mobileId;
	
	@com.google.gson.annotations.SerializedName("password")
	private String password = "";
	@com.google.gson.annotations.SerializedName("selfIntro")
	private String selfIntro = "";
	@com.google.gson.annotations.SerializedName("webPage")
	private String webPage = "";
	@com.google.gson.annotations.SerializedName("email")
	private String email = "";

	public ItUser() {
		super();
	}

	public ItUser(String itUserId, PLATFORM platform, String nickName, TYPE type, String registrationId, String mobileId) {
		super();
		this.itUserId = itUserId;
		this.platform = platform.toString();
		this.nickName = nickName;
		this.type = type.toString();
		this.registrationId = registrationId;
		this.mobileId = mobileId;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItUserId() {
		return itUserId;
	}
	public void setItUserId(String itUserId) {
		this.itUserId = itUserId;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSelfIntro() {
		return selfIntro;
	}
	public void setSelfIntro(String selfIntro) {
		this.selfIntro = selfIntro;
	}
	public String getWebPage() {
		return webPage;
	}
	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void fixType(TYPE type) {
		this.type = type.toString();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getMobileId() {
		return mobileId;
	}
	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}
	public void readItUser(ItUser itUser) {
		this.setId(itUser.getId());
		this.setItUserId(itUser.getItUserId());
		this.setPlatform(itUser.getPlatform());
		this.setEmail(itUser.getEmail());
		this.setNickName(itUser.getNickName());
		this.setSelfIntro(itUser.getSelfIntro());
		this.setWebPage(itUser.getWebPage());
		this.setType(itUser.getType());
		this.setPassword(itUser.getPassword());
		this.setRegistrationId(itUser.getRegistrationId());
		this.setMobileId(itUser.getMobileId());
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public boolean isLoggedIn() {
		return (this.id != null && !this.id.equals(PrefHelper.DEFAULT_STRING)); 
	}


	public boolean isMe(){
		return ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class).getId().equals(this.id);
	}


	public boolean isPro(){
		return TYPE.PRO.toString().equals(this.type);
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<ItUser> CREATOR = new Creator<ItUser>(){
		public ItUser createFromParcel(Parcel in){
			return new ItUser(in);
		}
		public ItUser[] newArray(int size){
			return new ItUser[size]; 
		}
	};

	public ItUser(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readItUser(new Gson().fromJson(in.readString(), ItUser.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
