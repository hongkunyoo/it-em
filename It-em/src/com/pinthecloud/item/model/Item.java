package com.pinthecloud.item.model;

import java.util.List;

import com.pinthecloud.item.util.RandomUtil;

public class Item extends AbstractItemModel<Item> {
	private List<Reply> replyList;
	private int replyCount;
	private List<LikeIt> likeItList;
	private int likeItCount;
	private String imgUrl;
	
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
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	@Override
	public Item rand() {
		Item item = super.rand();
//		item.setLikeItCount(RandomUtil.getInt());
//		item.setReplyCount(RandomUtil.getInt());
		item.setImgUrl(RandomUtil.getUrl());
		return item;
	}
	
	@Override
	public Item rand(boolean hasId) {
		Item item = super.rand(hasId);
//		item.setLikeItCount(RandomUtil.getInt());
//		item.setReplyCount(RandomUtil.getInt());
		item.setImgUrl(RandomUtil.getUrl());
		
		return item;
	}
}
