package com.pinthecloud.item.model;

import android.text.format.Time;

import com.pinthecloud.item.exception.ItException;

// 20141113014345 --> 2014-11-13 01:43:45
public class ItDateTime {
	private String dateTime;

	public ItDateTime(String rawDateTime) {
		if (rawDateTime.length() != 14) {
			throw new ItException(ItException.TYPE.FORMATE_ERROR);
		}
		this.dateTime = rawDateTime;
	}

	public int getYear() {
		return Integer.parseInt(dateTime.substring(0, 4));
	}
	public int getMonth() {
		return Integer.parseInt(dateTime.substring(4, 6));
	}
	public int getDate() {
		return Integer.parseInt(dateTime.substring(6, 8));
	}
	public int getHours() {
		return Integer.parseInt(dateTime.substring(8, 10));
	}
	public int getMinutes() {
		return Integer.parseInt(dateTime.substring(10, 12));
	}
	public int getSeconds() {
		return Integer.parseInt(dateTime.substring(12, 14));
	}
	public String toString() {
		return dateTime;
	}
	public String getElapsedTime() {
		Time time = new Time();
		Time nowTime = new Time();

		time.set(this.getSeconds(), this.getMinutes(), this.getHours(), this.getDate(), this.getMonth()-1, this.getYear());
		nowTime.setToNow();

		if(time.year == nowTime.year && time.month == nowTime.month && time.monthDay == nowTime.monthDay){
			if(time.hour == nowTime.hour){
				if(time.minute == nowTime.minute){
					return ""+(nowTime.second-time.second);
				} else {
					return ""+(nowTime.minute-time.minute);
				}
			} else {
				return ""+(nowTime.hour-time.hour);
			}
		} else {
			return this.toPrettyDate();	
		}
	}
	public static ItDateTime getToday() {
		Time time = new Time();
		time.setToNow();
		return new ItDateTime(time.format("%Y%m%d000000"));
	}
	public static ItDateTime getNow() {
		Time time = new Time();
		time.setToNow();
		return new ItDateTime(time.format("%Y%m%d%H%M%S"));
	}
	public ItDateTime getYesterday() {
		Time time = new Time();
		time.set(this.getDate()-1, this.getMonth()-1, this.getYear());
		return new ItDateTime(time.format("%Y%m%d000000"));
	}
	public String toPrettyDate() {
		Time time = new Time();
		time.set(this.getDate(), this.getMonth()-1, this.getYear());
		return time.format("%Y-%m-%d");
	}
	public String toDate() {
		Time time = new Time();
		time.set(this.getDate(), this.getMonth()-1, this.getYear());
		return time.format("%Y%m%d");
	}
	//	public String toPrettyDateTime() {
	//		Time time = new Time();
	//		time.set(this.getSeconds(), this.getMinutes(), this.getHours(), this.getDate(), this.getMonth()-1, this.getYear());
	//		return time.format("%Y-%m-%d(%H:%M:%S)");
	//	}
}
