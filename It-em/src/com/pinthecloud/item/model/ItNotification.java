package com.pinthecloud.item.model;

import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;

public class ItNotification extends AbstractItemModel<ItNotification> implements Parcelable {

	public static enum TYPE {
		LikeIt,
		Reply,
		ProductTag
	}

	private String refWhoMade;
	private String refWhoMadeId;
	private String type;
	private String typeRefId;
	private int imageNumber;
	private int imageWidth;
	private int imageHeight;

	public ItNotification() {
		super();
	}
	public ItNotification(String whoMade, String whoMadeId, String refId, String refWhoMade, String refWhoMadeId,
			String content, TYPE type, int imageNumber, int imageWidth, int imageHeight) {
		super();
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
		this.setRefWhoMade(refWhoMade);
		this.setRefWhoMadeId(refWhoMadeId);
		this.setContent(content);
		this.setType(type.toString());
		this.setImageNumber(imageNumber);
		this.setImageWidth(imageWidth);
		this.setImageHeight(imageHeight);
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
	public String getTypeRefId() {
		return typeRefId;
	}
	public void setTypeRefId(String typeRefId) {
		this.typeRefId = typeRefId;
	}
	public int getImageNumber() {
		return imageNumber;
	}
	public void setImageNumber(int imageNumber) {
		this.imageNumber = imageNumber;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
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
		this.setTypeRefId(noti.getTypeRefId());
		this.setImageNumber(noti.getImageNumber());
		this.setImageWidth(noti.getImageWidth());
		this.setImageHeight(noti.getImageHeight());
	}

	public String notiContent(){
		ItApplication app = ItApplication.getInstance();
		ItUser myItUser = app.getObjectPrefHelper().get(ItUser.class);

		String refWhoMade = "";
		if(getRefWhoMadeId().equals(myItUser.getId())){
			refWhoMade = app.getResources().getString(R.string.noti_name_title_my);
		} else {
			refWhoMade = getRefWhoMade() + app.getResources().getString(R.string.of);
		}

		String type = "";
		if(getType().equals(ItNotification.TYPE.LikeIt.toString())){
			type = app.getResources().getString(R.string.noti_like);
		} else if(getType().equals(ItNotification.TYPE.Reply.toString())){
			type = app.getResources().getString(R.string.noti_reply);
		} else if(getType().equals(ItNotification.TYPE.ProductTag.toString())){
			type = app.getResources().getString(R.string.noti_product_tag);
		}

		String content = "";
		if(getType().equals(ItNotification.TYPE.ProductTag.toString())){
			content = String.format(Locale.US, app.getResources().getString(R.string.noti_product_tag_content), refWhoMade, type);
		} else {
			content = String.format(Locale.US, app.getResources().getString(R.string.noti_general_content), getWhoMade(), refWhoMade, type);
		}

		return content;
	}

	public Item makeItem(){
		Item item = new Item();
		item.setId(refId);
		item.setWhoMade(refWhoMade);
		item.setWhoMadeId(refWhoMadeId);
		item.setImageNumber(imageNumber);
		item.setImageWidth(imageWidth);
		item.setImageHeight(imageHeight);
		return item;
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
