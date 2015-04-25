package com.pinthecloud.item.model;

import java.util.Locale;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;

public class ItNotification extends AbstractItemModel<ItNotification> {

	public static enum TYPE {
		ItLike,
		Reply,
		ProductTag
	}

	private String refWhoMade;
	private String refWhoMadeId;
	private String type;
	private String typeRefId;
	private int imageNumber;
	private int mainImageWidth;
	private int mainImageHeight;

	public ItNotification() {
		super();
	}
	public ItNotification(String whoMade, String whoMadeId, String refId, String refWhoMade, String refWhoMadeId,
			String content, TYPE type, int imageNumber, int mainImageWidth, int mainImageHeight) {
		super();
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
		this.setRefWhoMade(refWhoMade);
		this.setRefWhoMadeId(refWhoMadeId);
		this.setContent(content);
		this.setType(type.toString());
		this.setImageNumber(imageNumber);
		this.setMainImageWidth(mainImageWidth);
		this.setMainImageHeight(mainImageHeight);
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
	public int getMainImageWidth() {
		return mainImageWidth;
	}
	public void setMainImageWidth(int mainImageWidth) {
		this.mainImageWidth = mainImageWidth;
	}
	public int getMainImageHeight() {
		return mainImageHeight;
	}
	public void setMainImageHeight(int mainImageHeight) {
		this.mainImageHeight = mainImageHeight;
	}

	public String makeMessage(){
		ItApplication app = ItApplication.getInstance();
		String whoMadeString = getWhoMade();
		String refWhoMadeString = refWhoMadeString();
		String typeString = typeString();
		
		if(getType().equals(ItNotification.TYPE.ProductTag.toString())){
			return String.format(Locale.US, app.getResources().getString(R.string.noti_product_tag_message),
					refWhoMadeString, typeString);
		} else {
			return String.format(Locale.US, app.getResources().getString(R.string.noti_general_message),
					whoMadeString, refWhoMadeString, typeString);
		}
	}

	private String refWhoMadeString(){
		ItApplication app = ItApplication.getInstance();
		ItUser myUser = app.getObjectPrefHelper().get(ItUser.class);
		if(getRefWhoMadeId().equals(myUser.getId())){
			return app.getResources().getString(R.string.your);
		} else {
			return getRefWhoMade() + app.getResources().getString(R.string.of);
		}
	}

	private String typeString(){
		ItApplication app = ItApplication.getInstance();
		if(getType().equals(ItNotification.TYPE.ItLike.toString())){
			return app.getResources().getString(R.string.noti_like);
		} else if(getType().equals(ItNotification.TYPE.Reply.toString())){
			return app.getResources().getString(R.string.noti_reply);
		} else if(getType().equals(ItNotification.TYPE.ProductTag.toString())){
			return app.getResources().getString(R.string.noti_product_tag);
		} else {
			return "";
		}
	}

	public Item makeItem(){
		Item item = new Item();
		item.setId(refId);
		item.setWhoMade(refWhoMade);
		item.setWhoMadeId(refWhoMadeId);
		item.setImageNumber(imageNumber);
		item.setMainImageWidth(mainImageWidth);
		item.setMainImageHeight(mainImageHeight);
		return item;
	}
}
