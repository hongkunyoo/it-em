package com.pinthecloud.item.helper;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;

public class GAHelper {

	// Label
	public static final String HOME = "HOME";
	public static final String ITEM = "ITEM";
	public static final String PRODUCT_TAG = "PRODUCT TAG";
	public static final String MY_PAGE = "MY_PAGE";
	
	// Action
	public static final String VIEW_ITEM = "view item";
	public static final String LIKE = "like";
	public static final String LIKE_CANCEL = "like cancel";
	public static final String VIEW_PRODUCT_TAG = "view product tag";
	public static final String VIEW_UPLOADER = "view uploader";
	public static final String PRICE = "Price";
	
	
	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}

	private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	private final String PROPERTY_ID = "UA-53944359-2";
	private Context mContext;


	public GAHelper(Context context) {
		super();
		this.mContext = context;
	}

	private synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
							: analytics.newTracker(R.xml.ecommerce_tracker);
					mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}


	public void sendEvent(String category, String action, String label){
		if(!ItApplication.isDebugging()){
			getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.EventBuilder()
			.setCategory(category)
			.setAction(action)
			.setLabel(label)
			.build());
		}
	}


	public void sendTimeing(String category, String variable, String label){
		if(!ItApplication.isDebugging()){
			getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.TimingBuilder()
			.setCategory(category)
			.setVariable(variable)
			.setLabel(label)
			.build());
		}
	}


	public void reportActivityStart(Activity activity){
		if(!ItApplication.isDebugging()){
			GoogleAnalytics.getInstance(mContext).reportActivityStart(activity);
		}
	}


	public void reportActivityStop(Activity activity){
		if(!ItApplication.isDebugging()){
			GoogleAnalytics.getInstance(mContext).reportActivityStop(activity);
		}
	}
	
	public void sendScreen(Fragment frag){
		if(!ItApplication.isDebugging()){
			Tracker tracker = getTracker(TrackerName.APP_TRACKER);
			tracker.setScreenName(frag.getClass().getSimpleName());
			tracker.send(new HitBuilders.AppViewBuilder().build());
		}
	}
}
