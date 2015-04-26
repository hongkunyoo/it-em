package com.pinthecloud.item.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Item extends AbstractItemModel<Item> implements Parcelable {

	public static String INTENT_KEY = "ITEM_INTENT_KEY";

	private int replyCount;
	private List<Reply> replyList;
	private int likeCount;
	private String prevLikeId;
	private boolean hasProductTag;
	private List<ProductTag> productTagList;
	private int imageNumber;
	private int coverImageWidth;
	private int coverImageHeight;
	private int mainImageWidth;
	private int mainImageHeight;
	private ItUser whoMadeUser;

	public Item() {
		super();
	}
	public Item(String content, String whoMade, String whoMadeId, int imageNumber, 
			int coverImageWidth, int coverImageHeight, int mainImageWidth, int mainImageHeight) {
		super();
		this.setContent(content);
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setImageNumber(imageNumber);
		this.setCoverImageWidth(coverImageWidth);
		this.setCoverImageHeight(coverImageHeight);
		this.setMainImageWidth(mainImageWidth);
		this.setMainImageHeight(mainImageHeight);
	}
	public Item(String id, String whoMade, String whoMadeId, int imageNumber, int coverImageWidth, int coverImageHeight) {
		super();
		this.setId(id);
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setImageNumber(imageNumber);
		this.setCoverImageWidth(coverImageWidth);
		this.setCoverImageHeight(coverImageHeight);
	}

	@Override
	public String getRefId() {
		return null;
	}
	@Override
	public void setRefId(String refId) {
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public List<Reply> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<Reply> replyList) {
		this.replyList = replyList;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public boolean isHasProductTag() {
		return hasProductTag;
	}
	public void setHasProductTag(boolean hasProductTag) {
		this.hasProductTag = hasProductTag;
	}
	public List<ProductTag> getProductTagList() {
		return productTagList;
	}
	public void setProductTagList(List<ProductTag> productTagList) {
		this.productTagList = productTagList;
	}
	public int getImageNumber() {
		return imageNumber;
	}
	public void setImageNumber(int imageNumber) {
		this.imageNumber = imageNumber;
	}
	public int getCoverImageWidth() {
		return coverImageWidth;
	}
	public void setCoverImageWidth(int coverImageWidth) {
		this.coverImageWidth = coverImageWidth;
	}
	public int getCoverImageHeight() {
		return coverImageHeight;
	}
	public void setCoverImageHeight(int coverImageHeight) {
		this.coverImageHeight = coverImageHeight;
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
	public String getPrevLikeId() {
		return prevLikeId;
	}
	public void setPrevLikeId(String prevLikeId) {
		this.prevLikeId = prevLikeId;
	}
	public ItUser getWhoMadeUser() {
		return whoMadeUser;
	}
	public void setWhoMadeUser(ItUser whoMadeUser) {
		this.whoMadeUser = whoMadeUser;
	}
	public void readItem(Item item) {
		this.setId(item.getId());
		this.setContent(item.getContent());
		this.setWhoMade(item.getWhoMade());
		this.setWhoMadeId(item.getWhoMadeId());
		this.setRawCreateDateTime(item.getRawCreateDateTime());
		this.setRefId(item.getRefId());
		this.setReplyCount(item.getReplyCount());
		this.setReplyList(item.getReplyList());
		this.setLikeCount(item.getLikeCount());
		this.setHasProductTag(item.isHasProductTag());
		this.setProductTagList(item.getProductTagList());
		this.setImageNumber(item.getImageNumber());
		this.setCoverImageWidth(item.getCoverImageWidth());
		this.setCoverImageHeight(item.getCoverImageHeight());
		this.setMainImageWidth(item.getMainImageWidth());
		this.setMainImageHeight(item.getMainImageHeight());
		this.setPrevLikeId(item.getPrevLikeId());
		this.setWhoMadeUser(item.getWhoMadeUser());
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<Item> CREATOR = new Creator<Item>(){
		public Item createFromParcel(Parcel in){
			return new Item(in);
		}
		public Item[] newArray(int size){
			return new Item[size]; 
		}
	};

	public Item(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readItem(new Gson().fromJson(in.readString(), Item.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
