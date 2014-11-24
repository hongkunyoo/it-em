package com.pinthecloud.item;

import java.net.MalformedURLException;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.item.analysis.UserHabitHelper;
import com.pinthecloud.item.helper.AimHelper;
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
	private static AimHelper aimHelper;

	public ItApplication() {
		app = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		init();
	}
	
	private void init() {
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
		aimHelper = new AimHelper();
	}

	public static ItApplication getInstance(){
		return app;
	}
	public MobileServiceClient getMobileClient() {
		if (mClient == null) init();
		return mClient;
	}
	public UserHabitHelper getUserHabitHelper() {
		return userHabitHelper;
	}
	public PrefHelper getPrefHelper() {
		if (prefHelper == null) init();
		return prefHelper;
	}
	public ObjectPrefHelper getObjPrefHelper() {
		if (objPrefHelper == null) init();
		return objPrefHelper;
	}
	public AimHelper getAimHelper() {
		if (aimHelper == null) init();
		return aimHelper;
	}
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
}
