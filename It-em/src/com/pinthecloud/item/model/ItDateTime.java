package com.pinthecloud.item.model;

import java.util.TimeZone;

import android.text.format.Time;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.exception.ItException;

// 20141113014345 --> 2014-11-13 01:43:45
public class ItDateTime {

	private Time dateTime;

	public ItDateTime(String rawDateTime) {
		if (rawDateTime.length() != 14) {
			throw new ItException(ItException.TYPE.FORMATE_ERROR);
		}
		
		dateTime = new Time(Time.TIMEZONE_UTC);
		int year = Integer.parseInt(rawDateTime.substring(0, 4));
		int month = Integer.parseInt(rawDateTime.substring(4, 6));
		int date = Integer.parseInt(rawDateTime.substring(6, 8));
		int hour = Integer.parseInt(rawDateTime.substring(8, 10));
		int minute = Integer.parseInt(rawDateTime.substring(10, 12));
		int second = Integer.parseInt(rawDateTime.substring(12, 14));
		dateTime.set(second, minute, hour, date, month-1, year);
		dateTime.switchTimezone(TimeZone.getDefault().getID());
	}

	public static ItDateTime getToday() {
		Time time = new Time();
		time.setToNow();
		return new ItDateTime(time.format("%Y%m%d000000"));
	}

	public ItDateTime getYesterday() {
		dateTime.set(dateTime.monthDay-1, dateTime.month, dateTime.year);
		dateTime.normalize(true);
		return new ItDateTime(dateTime.format("%Y%m%d000000"));
	}

	public String toDate() {
		return dateTime.format("%Y%m%d");
	}

	public String toPrettyTime() {
		return dateTime.format("%H:%M");
	}

	public String toPrettyDate() {
		return dateTime.format("%Y-%m-%d");
	}

	public String toPrettyDateTime() {
		return dateTime.format("%Y-%m-%d %H:%M");
	}

	public int getElapsedDate() {
		Time nowTime = new Time();
		nowTime.setToNow();

		int nowJulianDay = Time.getJulianDay(nowTime.normalize(true), nowTime.gmtoff);
		int itJulianDay = Time.getJulianDay(dateTime.normalize(true), dateTime.gmtoff);
		return nowJulianDay - itJulianDay;
	}

	public String getElapsedDateTime() {
		Time nowTime = new Time();
		nowTime.setToNow();

		ItApplication app = ItApplication.getInstance();
		if(dateTime.year == nowTime.year && dateTime.month == nowTime.month && dateTime.monthDay == nowTime.monthDay){
			if(dateTime.hour == nowTime.hour){
				if(dateTime.minute == nowTime.minute){
					int elapsedSecond = nowTime.second-dateTime.second;
					if(elapsedSecond < 0)	elapsedSecond = 0;
					return elapsedSecond + app.getResources().getString(R.string.second_ago);
				} else {
					return (nowTime.minute-dateTime.minute) + app.getResources().getString(R.string.minute_ago);
				}
			} else {
				return (nowTime.hour-dateTime.hour) + app.getResources().getString(R.string.hour_ago);
			}
		} else {
			int elapsedDate = getElapsedDate();
			if(elapsedDate == 1){
				return app.getResources().getString(R.string.yesterday) + " " + toPrettyTime();
			} else if(elapsedDate < 4) {
				return elapsedDate + app.getResources().getString(R.string.day_ago) + " " + toPrettyTime();
			} else {
				return toPrettyDateTime();
			}
		}
	}

	@Override
	public String toString() {
		return dateTime.format("%Y%m%d%H%M%S");
	}
}
