package com.pinthecloud.item.model;


public class ItemHashTag extends AbstractItemModel<ItemHashTag>{
	
	public static String INTENT_KEY = "ITEM_HASH_TAG_INTENT_KEY";
	
	public ItemHashTag() {
		super();
	}
	public ItemHashTag(String content, String refId) {
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
