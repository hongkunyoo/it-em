package com.pinthecloud.item.event;

public class GCMRegIdEvent {

	private String regId;

	public GCMRegIdEvent() {
		super();
	}
	public GCMRegIdEvent(String regId) {
		super();
		this.regId = regId;
	}
	
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
}
