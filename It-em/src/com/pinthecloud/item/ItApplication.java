package com.pinthecloud.item;

import java.net.MalformedURLException;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.analysis.UserHabitHelper;
import com.pinthecloud.item.databases.AimDBHelper;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;

public class ItApplication extends Application {

	// Windows Azure Mobile Service Keys
	private final String AZURE_REAL_URL = "https://it-em.azure-mobile.net/";
	private final String AZURE_REAL_KEY = "dGxfhLUwSWWtSiuoFKqHvQelJMPLZp89";
	private final String AZURE_TEST_URL = "https://it-emtest.azure-mobile.net/";
	private final String AZURE_TEST_KEY = "yHCLhyMsjiaLSbcMKeUdUOoZkbYXfK52";

	// Mobile Service instances
	private static MobileServiceClient mClient;

	// Application
	private static ItApplication app;
	private static ProgressDialog progressDialog;

	// Analysis
	private static UserHabitHelper userHabitHelper;
	private static GAHelper gaHelper;

	// Helper
	private static PrefHelper prefHelper;
	private static ObjectPrefHelper objectPrefHelper;
	private static AimHelper aimHelper;
	private static AimDBHelper aimDBHelper;
	private static UserHelper userHelper;
	private static BlobStorageHelper blobStorageHelper;

	public ItApplication() {
		super();
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
		}

		userHabitHelper = new UserHabitHelper();
		gaHelper = new GAHelper(app);

		prefHelper = new PrefHelper(app);
		objectPrefHelper = new ObjectPrefHelper(app);
		aimHelper = new AimHelper(app);
		userHelper = new UserHelper(app);
		aimDBHelper = new AimDBHelper(app);
		blobStorageHelper = new BlobStorageHelper(app);
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
	public GAHelper getGaHelper() {
		return gaHelper;
	}
	public PrefHelper getPrefHelper() {
		if (prefHelper == null) init();
		return prefHelper;
	}
	public ObjectPrefHelper getObjectPrefHelper() {
		if (objectPrefHelper == null) init();
		return objectPrefHelper;
	}
	public AimHelper getAimHelper() {
		if (aimHelper == null) init();
		return aimHelper;
	}
	public AimDBHelper getAimDBHelper() {
		if (aimDBHelper == null) init();
		return aimDBHelper;
	}
	public UserHelper getUserHelper() {
		if (userHelper == null) init();
		return userHelper;
	}
	public BlobStorageHelper getBlobStorageHelper() {
		if (blobStorageHelper == null) init();
		return blobStorageHelper;
	}

	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}

	public void showProgressDialog(Context context){
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.show();
		progressDialog.setContentView(R.layout.custom_progress_dialog);
	}

	public void dismissProgressDialog(){
		progressDialog.dismiss();
	}
}
