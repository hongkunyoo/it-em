package com.pinthecloud.item.model;

import java.util.Locale;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;

public class ItNotification extends AbstractItemModel<ItNotification> {

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
	
	public String notiContent(){
		ItApplication app = ItApplication.getInstance();
		ItUser myItUser = app.getObjectPrefHelper().get(ItUser.class);

		String refWhoMade = "";
		if(getRefWhoMadeId().equals(myItUser.getId())){
			refWhoMade = app.getResources().getString(R.string.my_item);
		} else {
			refWhoMade = getRefWhoMade() + app.getResources().getString(R.string.of_item);
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
		item.setMainImageWidth(mainImageWidth);
		item.setMainImageHeight(mainImageHeight);
		return item;
	}
}
