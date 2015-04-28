package com.pinthecloud.item.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.helper.PrefHelper;

public class ItUser implements Parcelable {

	public static final String INTENT_KEY = "IT_USER_INTENT_KEY";
	public static final String MILEAGE_GUIDE_READ_KEY = "MILEAGE_GUIDE_READ_KEY";
	public static final String NOTIFICATION_NUMBER_KEY = "NOTIFICATION_NUMBER_KEY";
	
	public static enum PLATFORM {
		FACEBOOK,
		KAKAO
	}

	public static enum TYPE {
		VIEWER,
		SELLER
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
	@com.google.gson.annotations.SerializedName("selfIntro")
	private String selfIntro;
	@com.google.gson.annotations.SerializedName("webPage")
	private String webPage;
	@com.google.gson.annotations.SerializedName("notiMyItem")
	private boolean notiMyItem;
	@com.google.gson.annotations.SerializedName("notiItItem")
	private boolean notiItItem;
	@com.google.gson.annotations.SerializedName("notiReplyItem")
	private boolean notiReplyItem;
	@com.google.gson.annotations.SerializedName("email")
	private String email;
	@com.google.gson.annotations.SerializedName("bankName")
	private int bankName;
	@com.google.gson.annotations.SerializedName("bankAccountNumber")
	private String bankAccountNumber;
	@com.google.gson.annotations.SerializedName("bankAccountName")
	private String bankAccountName;
	@com.google.gson.annotations.SerializedName("mileage")
	private int mileage;

	public ItUser() {
		super();
	}
	public ItUser(String itUserId, PLATFORM platform, String nickName, TYPE type) {
		super();
		this.itUserId = itUserId;
		this.platform = platform.toString();
		this.nickName = nickName;
		this.type = type.toString();
		this.selfIntro = "";
		this.webPage = "";
		this.notiMyItem = true;
		this.notiItItem = true;
		this.notiReplyItem = true;
		this.email = "";
		this.bankName = -1;
		this.bankAccountNumber = "";
		this.bankAccountName = "";
		this.mileage = 0;
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
	public boolean isNotiMyItem() {
		return notiMyItem;
	}
	public void setNotiMyItem(boolean notiMyItem) {
		this.notiMyItem = notiMyItem;
	}
	public boolean isNotiItItem() {
		return notiItItem;
	}
	public void setNotiItItem(boolean notiItItem) {
		this.notiItItem = notiItItem;
	}
	public boolean isNotiReplyItem() {
		return notiReplyItem;
	}
	public void setNotiReplyItem(boolean notiReplyItem) {
		this.notiReplyItem = notiReplyItem;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getBankName() {
		return bankName;
	}
	public String bankNameString(Context context){
		String[] bankNameArray = context.getResources().getStringArray(R.array.bank_name_array);
		return bankNameArray[bankName+1];
	}
	public void setBankName(int bankName) {
		this.bankName = bankName;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public String getBankAccountName() {
		return bankAccountName;
	}
	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}
	public int getMileage() {
		return mileage;
	}
	public void setMileage(int mileage) {
		this.mileage = mileage;
	}
	public void readItUser(ItUser itUser) {
		this.setId(itUser.getId());
		this.setItUserId(itUser.getItUserId());
		this.setPlatform(itUser.getPlatform());
		this.setNickName(itUser.getNickName());
		this.setSelfIntro(itUser.getSelfIntro());
		this.setWebPage(itUser.getWebPage());
		this.setType(itUser.getType());
		this.setNotiMyItem(itUser.isNotiMyItem());
		this.setNotiItItem(itUser.isNotiItItem());
		this.setNotiReplyItem(itUser.isNotiReplyItem());
		this.setEmail(itUser.getEmail());
		this.setBankName(itUser.getBankName());
		this.setBankAccountNumber(itUser.getBankAccountNumber());
		this.setBankAccountName(itUser.getBankAccountName());
		this.setMileage(itUser.getMileage());
	}

	public boolean checkLoggedIn() {
		return (this.itUserId != null && !this.itUserId.equals(PrefHelper.DEFAULT_STRING)); 
	}

	public boolean checkMe(){
		return ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class).getItUserId().equals(this.itUserId);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
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
