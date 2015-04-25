package com.pinthecloud.item.model;

public class ItLike extends AbstractItemModel<ItLike>{

	public ItLike() {
		super();
	}
	public ItLike(String id) {
		super();
		this.setId(id);
	}
	public ItLike(String whoMade, String whoMadeId, String refId) {
		super();
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
	}

	@Override
	public String getContent() {
		return null;
	}
	@Override
	public void setContent(String content) {
	}
}
