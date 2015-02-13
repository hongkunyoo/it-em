package com.pinthecloud.item.event;

public class GcmRegistrationIdEvent {
	
	private String registrationId;

	public GcmRegistrationIdEvent() {
		super();
	}
	public GcmRegistrationIdEvent(String registrationId) {
		super();
		this.registrationId = registrationId;
	}

	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
}
