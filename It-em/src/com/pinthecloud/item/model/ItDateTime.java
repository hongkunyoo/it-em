package com.pinthecloud.item.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

import com.pinthecloud.item.R;

// 20141113014345 --> 2014-11-13 01:43:45
public class ItDateTime {

	private final int DAY_SECOND = 86400;
	private final int HOUR_SECOND = 3600;
	private final int MINUTE_SECOND = 60;

	private Calendar calendar;

	public ItDateTime(String rawDateTime) {
		int year = Integer.parseInt(rawDateTime.substring(0, 4));
		int month = Integer.parseInt(rawDateTime.substring(4, 6));
		int day = Integer.parseInt(rawDateTime.substring(6, 8));
		int hour = Integer.parseInt(rawDateTime.substring(8, 10));
		int minute = Integer.parseInt(rawDateTime.substring(10, 12));
		int second = Integer.parseInt(rawDateTime.substring(12, 14));

		calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.set(year, month-1, day, hour, minute, second);
		calendar.getTime();
		calendar.setTimeZone(TimeZone.getDefault());
	}

	public String toPrettyTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
		return format.format(calendar.getTime());
	}

	public String toPrettyDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
		return format.format(calendar.getTime());
	}

	public int getElapsedSecond() {
		Calendar nowCalendar = new GregorianCalendar();
		return (int) ((nowCalendar.getTime().getTime() - calendar.getTime().getTime()) / 1000);
	}

	public String getElapsedTimeString(Context context) {
		int elapsedSecond = getElapsedSecond();
		int elapsedDate = elapsedSecond / DAY_SECOND;
		if(elapsedSecond < DAY_SECOND){
			// In a day
			if(elapsedSecond/HOUR_SECOND > 0){
				return (elapsedSecond/HOUR_SECOND) + context.getResources().getString(R.string.hour_ago);
			} else if(elapsedSecond/MINUTE_SECOND > 0) {
				return (elapsedSecond/MINUTE_SECOND) + context.getResources().getString(R.string.minute_ago);
			} else {
				if(elapsedSecond < 0) elapsedSecond = 0;
				return elapsedSecond + context.getResources().getString(R.string.second_ago);
			}
		} else if(elapsedDate == 1) {
			// 1 day
			return context.getResources().getString(R.string.yesterday) + " " + toPrettyTime();
		} else if(elapsedDate == 2) {
			// 2 day
			return elapsedDate + context.getResources().getString(R.string.day_ago) + " " + toPrettyTime();
		} else {
			// More than 3 day
			return toPrettyDateTime();
		}
	}

	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
		return format.format(calendar.getTime());
	}
}
