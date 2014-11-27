package com.pinthecloud.item.model;



public class Reply extends AbstractItemModel<Reply> {
	
	public Reply() {
		
	}
	public Reply(String content, String whoMade, String whoMadeId, String refId) {
		this.setContent(content);
		this.setWhoMade(whoMadeId);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
	}
}
