package com.pinthecloud.item;

import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.item.analysis.GAHelper;
import com.pinthecloud.item.analysis.UserHabitHelper;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.squareup.picasso.Picasso;

public class ItApplication extends Application {

	// Windows Azure Mobile Service Keys
	private final String AZURE_REAL_URL = "https://it-em.azure-mobile.net/";
	private final String AZURE_REAL_KEY = "TnmDvNkgfghvrcXjoQhRjEdcyFCEzd99";
	private final String AZURE_TEST_URL = "https://it-em-test.azure-mobile.net/";
	private final String AZURE_TEST_KEY = "jidjLSdrpbivsOXwsQStSSHGIKxhKa66";

	// Mobile Service
	private MobileServiceClient mClient;
	private MobileServiceClient realClient;
	private MobileServiceClient testClient;

	// Application
	private static ItApplication app;
	private ProgressDialog progressDialog;

	// Analysis
	private UserHabitHelper userHabitHelper;
	private GAHelper gaHelper;

	// Helper
	private Picasso picasso;
	private PrefHelper prefHelper;
	private ObjectPrefHelper objectPrefHelper;
	private AimHelper aimHelper;
	private UserHelper userHelper;
	private BlobStorageHelper blobStorageHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		
		mClient = getMobileClient();

		userHabitHelper = getUserHabitHelper();
		gaHelper = getGaHelper();

		picasso = getPicasso();
		prefHelper = getPrefHelper();
		objectPrefHelper = getObjectPrefHelper();
		aimHelper = getAimHelper();
		userHelper = getUserHelper();
		blobStorageHelper = getBlobStorageHelper();
	}

	public static ItApplication getInstance(){
		return app;
	}
	public MobileServiceClient getMobileClient() {
		if(mClient == null){
//			String AZURE_URL;
//			String AZURE_KEY;
//			if (GlobalVariable.DEBUG_MODE) {
//				AZURE_URL = AZURE_TEST_URL;
//				AZURE_KEY = AZURE_TEST_KEY;
//			} else {
//				AZURE_URL = AZURE_REAL_URL;
//				AZURE_KEY = AZURE_REAL_KEY;
//			}

			try {
				realClient = new MobileServiceClient(
						AZURE_REAL_URL,
						AZURE_REAL_KEY,
						this);
				testClient = new MobileServiceClient(
						AZURE_TEST_URL,
						AZURE_TEST_KEY,
						this);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			mClient = testClient;
		}
		return mClient;
	}
	public static boolean isDebugging() {
		return ItApplication.getInstance().mClient == ItApplication.getInstance().testClient;
	}
	public boolean isAdmin() {
		ArrayList<String> list = new ArrayList<String>(){
			private static final long serialVersionUID = 1L;
		{
			add("745959165517638"); // SeungMin
			add("834118693318943"); // ChaeSoo
			add("677830442331776"); // HongKun
			add("1536364146612739"); // PintheCloud
		}};
		ItUser user = objectPrefHelper.get(ItUser.class);
		if (user == null) return false;
 		String myItUserId = user.getItUserId();
		return list.contains(myItUserId);
	}
	public static int REAL = 0;
	public static int TEST = 1;
	public void switchClient(int type, final EntityCallback<Boolean> callback) {
		if (type == REAL) {
			mClient = realClient;
		} else {
			mClient = testClient;
		}
		
		userHelper.setMobileClient(mClient);
		aimHelper.setMobileClient(mClient);
		
		ItUser user = objectPrefHelper.get(ItUser.class);
		userHelper.getByItUserId(user.getItUserId(), new EntityCallback<ItUser>() {
			
			@Override
			public void onCompleted(ItUser entity) {
				// TODO Auto-generated method stub
				objectPrefHelper.put(entity);
				callback.onCompleted(true);
			}
		});
		
	}
	public UserHabitHelper getUserHabitHelper() {
		if(userHabitHelper == null) userHabitHelper = new UserHabitHelper();
		return userHabitHelper;
	}
	public GAHelper getGaHelper() {
		if(gaHelper == null) gaHelper = new GAHelper(app);
		return gaHelper;
	}
	public Picasso getPicasso() {
		if(picasso == null) picasso = Picasso.with(app);
		return picasso; 
	}
	public PrefHelper getPrefHelper() {
		if(prefHelper == null) prefHelper = new PrefHelper(app);
		return prefHelper;
	}
	public ObjectPrefHelper getObjectPrefHelper() {
		if(objectPrefHelper == null) objectPrefHelper = new ObjectPrefHelper(app);
		return objectPrefHelper;
	}
	public AimHelper getAimHelper() {
		if(aimHelper == null) aimHelper = new AimHelper(app);
		return aimHelper;
	}
	public UserHelper getUserHelper() {
		if(userHelper == null) userHelper = new UserHelper(app);
		return userHelper;
	}
	public BlobStorageHelper getBlobStorageHelper() {
		if(blobStorageHelper == null)blobStorageHelper = new BlobStorageHelper(app);
		return blobStorageHelper;
	}

	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
