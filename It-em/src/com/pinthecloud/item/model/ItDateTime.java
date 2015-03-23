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

		calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		calendar.set(year, month, day, hour, minute, second);
		calendar.setTimeZone(TimeZone.getDefault());
	}

	public static ItDateTime getToday() {
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd000000", Locale.US);
		return new ItDateTime(format.format(calendar.getTime()));
	}

	public ItDateTime getYesterday() {
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd000000", Locale.US);
		return new ItDateTime(format.format(calendar.getTime()));
	}

	public String toDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
		return format.format(calendar.getTime());
	}

	public String toPrettyTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
		return format.format(calendar.getTime());
	}

	public String toPrettyDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return format.format(calendar.getTime());
	}

	public String toPrettyDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		return format.format(calendar.getTime());
	}

	public int getElapsedDate() {
		Calendar nowCalendar = new GregorianCalendar();
		return (int) ((nowCalendar.getTime().getTime() - calendar.getTime().getTime()) / (1000*60*60*24));
	}

	public String getElapsedTime(Context context, int elapsedSecond){
		if(elapsedSecond/HOUR_SECOND > 0){
			return (elapsedSecond/HOUR_SECOND) + context.getResources().getString(R.string.hour_ago);
		} else if(elapsedSecond/MINUTE_SECOND > 0) {
			return (elapsedSecond/MINUTE_SECOND) + context.getResources().getString(R.string.minute_ago);
		} else {
			if(elapsedSecond < 0) elapsedSecond = 0;
			return elapsedSecond + context.getResources().getString(R.string.second_ago);
		}
	}

	public String getElapsedDateTime(Context context) {
		Calendar nowCalendar = new GregorianCalendar();

		int elapsedDate = getElapsedDate();
		if(elapsedDate < 1){
			// In a day
			int nowSecond = nowCalendar.get(Calendar.SECOND) +
					nowCalendar.get(Calendar.MINUTE)*MINUTE_SECOND +
					nowCalendar.get(Calendar.HOUR_OF_DAY)*HOUR_SECOND;
			int calendarSecond = calendar.get(Calendar.SECOND) +
					calendar.get(Calendar.MINUTE)*MINUTE_SECOND +
					calendar.get(Calendar.HOUR_OF_DAY)*HOUR_SECOND;
			return getElapsedTime(context, nowSecond - calendarSecond);
		} else if(elapsedDate == 1) {
			int nowSecond = nowCalendar.get(Calendar.SECOND) +
					nowCalendar.get(Calendar.MINUTE)*MINUTE_SECOND +
					nowCalendar.get(Calendar.HOUR_OF_DAY)*HOUR_SECOND +
					DAY_SECOND;
			int calendarSecond = calendar.get(Calendar.SECOND) +
					calendar.get(Calendar.MINUTE)*MINUTE_SECOND +
					calendar.get(Calendar.HOUR_OF_DAY)*HOUR_SECOND;
			int elapsedSeoncd = nowSecond - calendarSecond;

			if(elapsedSeoncd < DAY_SECOND){
				// In a day
				return getElapsedTime(context, elapsedSeoncd);
			} else {
				// 1 day
				return context.getResources().getString(R.string.yesterday) + " " + toPrettyTime();
			}
		} else if(elapsedDate < 3) {
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
