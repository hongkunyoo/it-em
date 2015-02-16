package com.pinthecloud.item.model;

public class LikeIt extends AbstractItemModel<LikeIt>{

	public LikeIt() {
		super();
	}
	public LikeIt(String id) {
		super();
		this.setId(id);
	}
	public LikeIt(String whoMade, String whoMadeId, String refId) {
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
