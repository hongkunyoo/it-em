package com.pinthecloud.item;

import java.net.MalformedURLException;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.item.analysis.UserHabitHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;

public class ItApplication extends Application {

	// Windows Azure Mobile Service Keys


	private final String AZURE_REAL_URL = "https://it-em.azure-mobile.net/";
	private final String AZURE_REAL_KEY = "dGxfhLUwSWWtSiuoFKqHvQelJMPLZp89";
	private final String AZURE_TEST_URL = "https://it-emtest.azure-mobile.net/";
	private final String AZURE_TEST_KEY = "yHCLhyMsjiaLSbcMKeUdUOoZkbYXfK52";

	// Application
	private static ItApplication app;

	// Mobile Service instances
	private static MobileServiceClient mClient;

	// Analysis
	private static UserHabitHelper userHabitHelper;
	private static PrefHelper prefHelper;
	private static ObjectPrefHelper objPrefHelper;

	public ItApplication() {
		app = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		String AZURE_URL;
		String AZURE_KEY;
		if (GlobalVariable.DEBUG_MODE) {
			AZURE_URL = AZURE_TEST_URL;
			AZURE_KEY = AZURE_TEST_KEY;
		} else {
			AZURE_URL = AZURE_REAL_URL;
			AZURE_KEY = AZURE_REAL_KEY;
		}

		try {
			mClient = new MobileServiceClient(
					AZURE_URL,
					AZURE_KEY,
					app);
		} catch (MalformedURLException e) {
			// Do nothing
		}

		userHabitHelper = new UserHabitHelper();
		prefHelper = new PrefHelper(app);
		objPrefHelper = new ObjectPrefHelper(app);
	}

	public static ItApplication getInstance(){
		return app;
	}
	public static MobileServiceClient getMobileClient() {
		return mClient;
	}
	public UserHabitHelper getUserHabitHelper() {
		return userHabitHelper;
	}
	public PrefHelper getPrefHelper() {
		if (prefHelper == null)
			prefHelper = new PrefHelper(app);
		return prefHelper;
	}
	public ObjectPrefHelper getObjPrefHelper() {
		if (objPrefHelper == null) objPrefHelper = new ObjectPrefHelper(app);
		return objPrefHelper;
	}

	
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
}
