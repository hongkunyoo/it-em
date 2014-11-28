package com.pinthecloud.item.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Item extends AbstractItemModel<Item> implements Parcelable{
	private List<Reply> replyList;
	private int replyCount;
	private List<LikeIt> likeItList;
	private int likeItCount;
	public static String INTENT_KEY = "ITEM_INTENT_KEY";
	
	public Item() {
		
	}
	public Item(String content, String whoMade, String whoMadeId) {
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
	public void setItem(Item other) {
		this.setId(other.getId());
		this.setContent(other.getContent());
		this.setWhoMade(other.getWhoMade());
		this.setWhoMadeId(other.getWhoMadeId());
		this.setRawCreateDateTime(other.getRawCreateDateTime());
		this.setRefId(other.getRefId());
		this.setReplyList(other.getReplyList());
		this.setReplyCount(other.getReplyCount());
		this.setLikeItList(other.getLikeItList());
		this.setLikeItCount(other.getLikeItCount());
	}
	
	@Override
	public Item rand() {
		Item item = super.rand();
//		item.setLikeItCount(RandomUtil.getInt());
//		item.setReplyCount(RandomUtil.getInt());
		return item;
	}
	
	@Override
	public Item rand(boolean hasId) {
		Item item = super.rand(hasId);
//		item.setLikeItCount(RandomUtil.getInt());
//		item.setReplyCount(RandomUtil.getInt());
		return item;
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
		this.setItem(new Gson().fromJson(in.readString(), Item.class));
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
