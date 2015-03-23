package com.pinthecloud.item.event;

import com.pinthecloud.item.model.ItNotification;

public class NotificationEvent {
	
	private ItNotification noti;

	public ItNotification getNoti() {
		return noti;
	}
	public void setNoti(ItNotification noti) {
		this.noti = noti;
	}

	public NotificationEvent() {
		super();
	}
	public NotificationEvent(ItNotification noti) {
		super();
		this.noti = noti;
	}
	
}
