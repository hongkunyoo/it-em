package com.pinthecloud.item.model;

import java.util.List;

public class Item extends AbstractItemModel<Item> {
	private List<Reply> replyList;
	private List<Like> likeList;
	
	public List<Reply> getReplyList() {
		return replyList;
	}

	public void setReplyList(List<Reply> replyList) {
		this.replyList = replyList;
	}

	public List<Like> getLikeList() {
		return likeList;
	}

	public void setLikeList(List<Like> likeList) {
		this.likeList = likeList;
	}
}
