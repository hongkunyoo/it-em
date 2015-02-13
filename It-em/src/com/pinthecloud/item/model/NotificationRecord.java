package com.pinthecloud.item.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class NotificationRecord implements Parcelable {

	public static enum TYPE {
		LIKE,
		COMMENT,
		PRODUCT_TAG
	}

	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("rawCreateDateTime")
	private String rawCreateDateTime;
	@com.google.gson.annotations.SerializedName("sender")
	private String sender;
	@com.google.gson.annotations.SerializedName("senderId")
	private String senderId;
	@com.google.gson.annotations.SerializedName("refId")
	private String refId;
	@com.google.gson.annotations.SerializedName("refWhoMade")
	private String refWhoMade;
	@com.google.gson.annotations.SerializedName("refWhoMadeId")
	private String refWhoMadeId;
	@com.google.gson.annotations.SerializedName("type")
	private String type;
	@com.google.gson.annotations.SerializedName("content")
	private String content;
	
	public NotificationRecord() {
		super();
	}
	public NotificationRecord(String id, String rawCreateDateTime, String sender, String senderId, String refId,
			String refWhoMade, String refWhoMadeId, String type, String content) {
		super();
		this.id = id;
		this.rawCreateDateTime = rawCreateDateTime;
		this.sender = sender;
		this.senderId = senderId;
		this.refId = refId;
		this.refWhoMade = refWhoMade;
		this.refWhoMadeId = refWhoMadeId;
		this.type = type;
		this.content = content;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRawCreateDateTime() {
		return rawCreateDateTime;
	}
	public void setRawCreateDateTime(String rawCreateDateTime) {
		this.rawCreateDateTime = rawCreateDateTime;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getRefWhoMade() {
		return refWhoMade;
	}
	public void setRefWhoMade(String refWhoMade) {
		this.refWhoMade = refWhoMade;
	}
	public String getRefWhoMadeId() {
		return refWhoMadeId;
	}
	public void setRefWhoMadeId(String refWhoMadeId) {
		this.refWhoMadeId = refWhoMadeId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void readNotificationRecord(NotificationRecord noti) {
		this.setId(noti.getId());
		this.setRawCreateDateTime(noti.getRawCreateDateTime());
		this.setSender(noti.getSender());
		this.setSenderId(noti.getSenderId());
		this.setRefId(noti.getRefId());
		this.setRefWhoMade(noti.getRefWhoMade());
		this.setRefWhoMadeId(noti.getRefWhoMadeId());
		this.setType(noti.getType());
		this.setContent(noti.getContent());
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<NotificationRecord> CREATOR = new Creator<NotificationRecord>(){
		public NotificationRecord createFromParcel(Parcel in){
			return new NotificationRecord(in);
		}
		public NotificationRecord[] newArray(int size){
			return new NotificationRecord[size]; 
		}
	};

	public NotificationRecord(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readNotificationRecord(new Gson().fromJson(in.readString(), NotificationRecord.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
