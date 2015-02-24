package com.pinthecloud.item.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class ItNotification extends AbstractItemModel<ItNotification> implements Parcelable {

	public static enum TYPE {
		LikeIt,
		Reply,
		ProductTag
	}

	private String refWhoMade;
	private String refWhoMadeId;
	private String type;

	public ItNotification() {
		super();
	}
	public ItNotification(String whoMade, String whoMadeId, String refId, String refWhoMade, String refWhoMadeId, String content, TYPE type) {
		super();
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
		this.setRefWhoMade(refWhoMade);
		this.setRefWhoMadeId(refWhoMadeId);
		this.setContent(content);
		this.setType(type.toString());
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
	public void fixType(TYPE type) {
		this.type = type.toString();
	}
	public void readNotiRecord(ItNotification noti) {
		this.setId(noti.getId());
		this.setRawCreateDateTime(noti.getRawCreateDateTime());
		this.setWhoMade(noti.getWhoMade());
		this.setWhoMadeId(noti.getWhoMadeId());
		this.setRefId(noti.getRefId());
		this.setRefWhoMade(noti.getRefWhoMade());
		this.setRefWhoMadeId(noti.getRefWhoMadeId());
		this.setContent(noti.getContent());
		this.setType(noti.getType());
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<ItNotification> CREATOR = new Creator<ItNotification>(){
		public ItNotification createFromParcel(Parcel in){
			return new ItNotification(in);
		}
		public ItNotification[] newArray(int size){
			return new ItNotification[size]; 
		}
	};

	public ItNotification(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readNotiRecord(new Gson().fromJson(in.readString(), ItNotification.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
