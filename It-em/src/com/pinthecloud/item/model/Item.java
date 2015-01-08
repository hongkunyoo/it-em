package com.pinthecloud.item.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.pinthecloud.item.ItApplication;

public class Item extends AbstractItemModel<Item> implements Parcelable {

	public static String INTENT_KEY = "ITEM_INTENT_KEY";

	private List<Reply> replyList;
	private int replyCount;
	private List<LikeIt> likeItList;
	private int likeItCount;

	public Item() {
		super();
	}
	public Item(String content, String whoMade, String whoMadeId) {
		super();
		this.setContent(content);
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
	}

	public String getRefId() {
		return null;
	}
	public void setRefId(String refId) {
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public int getLikeItCount() {
		return likeItCount;
	}
	public void setLikeItCount(int likeCount) {
		this.likeItCount = likeCount;
	}
	public List<Reply> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<Reply> replyList) {
		this.replyList = replyList;
	}
	public List<LikeIt> getLikeItList() {
		return likeItList;
	}
	public void setLikeItList(List<LikeIt> likeList) {
		this.likeItList = likeList;
	}
	public void readItem(Item item) {
		this.setId(item.getId());
		this.setContent(item.getContent());
		this.setWhoMade(item.getWhoMade());
		this.setWhoMadeId(item.getWhoMadeId());
		this.setRawCreateDateTime(item.getRawCreateDateTime());
		this.setRefId(item.getRefId());
		this.setReplyList(item.getReplyList());
		this.setReplyCount(item.getReplyCount());
		this.setLikeItList(item.getLikeItList());
		this.setLikeItCount(item.getLikeItCount());
	}


	@Override
	public Item rand() {
		Item item = super.rand();
		return item;
	}


	@Override
	public Item rand(boolean hasId) {
		Item item = super.rand(hasId);
		return item;
	}


	public boolean isMine(){
		return ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class).getId().equals(this.whoMadeId);
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
