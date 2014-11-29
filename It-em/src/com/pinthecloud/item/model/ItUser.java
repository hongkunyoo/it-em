package com.pinthecloud.item.model;

import com.google.gson.Gson;
import com.pinthecloud.item.helper.PrefHelper;

public class ItUser {
	
	public static final String INTENT_KEY = "IT_USER_INTENT_KEY";
	
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("itUserId")
	private String itUserId;
	@com.google.gson.annotations.SerializedName("nickName")
	private String nickName;
	@com.google.gson.annotations.SerializedName("selfIntro")
	private String selfIntro;
	@com.google.gson.annotations.SerializedName("webPage")
	private String webPage;
	
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
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	public boolean isLoggedIn() {
		return (this.id != null && !this.id.equals(PrefHelper.DEFAULT_STRING)); 
	}
}
