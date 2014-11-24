package com.pinthecloud.item.model;

import java.util.List;

public class Item extends AbstractItemModel<Item> {
	private List<Reply> replyList;
	private int replyCount;
	private List<LikeIt> likeItList;
	private int likeItCount;
	
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
}
