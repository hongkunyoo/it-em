package com.pinthecloud.item.model;


public class HashTag extends AbstractItemModel<HashTag>{
	
	public static String INTENT_KEY = "ITEM_HASH_TAG_INTENT_KEY";
	
	public HashTag() {
		super();
	}
	public HashTag(String content, String refId) {
		super();
		this.content = content;
		this.refId = refId;
	}

	@Override
	public final String getWhoMade() {
		return null;
	}
	@Override
	public final void setWhoMade(String whoMade) {
	}
	@Override
	public final String getWhoMadeId() {
		return null;
	}
	@Override
	public final void setWhoMadeId(String whoMadeId) {
	}
}
