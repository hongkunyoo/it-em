package com.pinthecloud.item.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class NotiRecord extends AbstractItemModel<NotiRecord> implements Parcelable {

	public static enum TYPE {
		LikeIt,
		Reply,
		ProductTag
	}

	private List<String> whoMadeList;
	private String refWhoMade;
	private String refWhoMadeId;
	private String type;

	public NotiRecord() {
		super();
	}
	public NotiRecord(String refId, String refWhoMade, String refWhoMadeId, String content, TYPE type) {
		super();
		this.setRefId(refId);
		this.setRefWhoMade(refWhoMade);
		this.setRefWhoMadeId(refWhoMadeId);
		this.setContent(content);
		this.setType(type.toString());
	}

	@Override
	public String getWhoMade() {
		return null;
	}
	@Override
	public void setWhoMade(String whoMade) {
	}
	@Override
	public String getWhoMadeId() {
		return null;
	}
	@Override
	public void setWhoMadeId(String whoMadeId) {
	}
	public List<String> getWhoMadeList() {
		return whoMadeList;
	}
	public void setWhoMadeList(List<String> whoMadeList) {
		this.whoMadeList = whoMadeList;
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
	public void readNotiRecord(NotiRecord noti) {
		this.setId(noti.getId());
		this.setRawCreateDateTime(noti.getRawCreateDateTime());
		this.setWhoMadeList(noti.getWhoMadeList());
		this.setRefId(noti.getRefId());
		this.setRefWhoMade(noti.getRefWhoMade());
		this.setRefWhoMadeId(noti.getRefWhoMadeId());
		this.setContent(noti.getContent());
		this.setType(noti.getType());
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<NotiRecord> CREATOR = new Creator<NotiRecord>(){
		public NotiRecord createFromParcel(Parcel in){
			return new NotiRecord(in);
		}
		public NotiRecord[] newArray(int size){
			return new NotiRecord[size]; 
		}
	};

	public NotiRecord(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readNotiRecord(new Gson().fromJson(in.readString(), NotiRecord.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
