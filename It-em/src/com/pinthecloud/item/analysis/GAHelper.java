package com.pinthecloud.item.analysis;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.R;

public class GAHelper {

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
	private Context context;


	public GAHelper(Context context) {
		super();
		this.context = context;
	}

	private synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
							: analytics.newTracker(R.xml.ecommerce_tracker);
					mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}


	public void sendEventGA(String category, String action, String label){
		if(!GlobalVariable.DEBUG_MODE){
			getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.EventBuilder()
			.setCategory(category)
			.setAction(action)
			.setLabel(label)
			.build());
		}
	}


	public void sendTimeingGA(String category, String variable, String label){
		if(!GlobalVariable.DEBUG_MODE){
			getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.TimingBuilder()
			.setCategory(category)
			.setVariable(variable)
			.setLabel(label)
			.build());
		}
	}


	public void sendScreenGA(String name){
		if(!GlobalVariable.DEBUG_MODE){
			Tracker tracker = getTracker(TrackerName.APP_TRACKER);
			tracker.setScreenName(name);
			tracker.send(new HitBuilders.AppViewBuilder().build());
		}
	}


	public void reportActivityStart(Activity activity){
		if(!GlobalVariable.DEBUG_MODE){
			GoogleAnalytics.getInstance(context).reportActivityStart(activity);
		}
	}


	public void reportActivityStop(Activity activity){
		if(!GlobalVariable.DEBUG_MODE){
			GoogleAnalytics.getInstance(context).reportActivityStop(activity);
		}
	}
}
