package com.pinthecloud.item.model;


public class HashTag extends AbstractItemModel<HashTag>{
	
	public static String INTENT_KEY = "HASH_TAG_INTENT_KEY";
	
	public HashTag() {
		super();
	}
	public HashTag(String content) {
		super();
		this.content = content;
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
