package com.pinthecloud.item.model;




public class Reply extends AbstractItemModel<Reply> {

	public Reply() {
		super();
	}
	
	public Reply(String content, String whoMade, String whoMadeId, String refId) {
		super();
		this.setContent(content);
		this.setWhoMade(whoMade);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
	}
}
