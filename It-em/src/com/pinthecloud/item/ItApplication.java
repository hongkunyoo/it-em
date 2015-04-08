package com.pinthecloud.item;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kakao.AuthType;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.DeviceHelper;
import com.pinthecloud.item.helper.GAHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.helper.UserHelper;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;
import com.squareup.picasso.Picasso;

@ReportsCrashes(formKey = "", mailTo="item@pinthecloud.com", mode = ReportingInteractionMode.TOAST, resToastText=R.string.error_report_message,
customReportContent = {ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.LOGCAT})
public class ItApplication extends Application {

	// Admin
	public static int REAL = 0;
	public static int TEST = 1;
	private ArrayList<String> adminList;

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
	private GAHelper gaHelper;

	// Helper
	private Picasso picasso;
	private PrefHelper prefHelper;
	private ObjectPrefHelper objectPrefHelper;
	private AimHelper aimHelper;
	private UserHelper userHelper;
	private DeviceHelper deviceHelper;
	private BlobStorageHelper blobStorageHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;

		//		ACRA.init(app);
		com.kakao.Session.initialize(this, AuthType.KAKAO_TALK);

		mClient = getMobileClient();
		gaHelper = getGaHelper();
		picasso = getPicasso();
		prefHelper = getPrefHelper();
		objectPrefHelper = getObjectPrefHelper();
		aimHelper = getAimHelper();
		userHelper = getUserHelper();
		deviceHelper = getDeviceHelper();
		blobStorageHelper = getBlobStorageHelper();
	}

	public static ItApplication getInstance(){
		return app;
	}
	public MobileServiceClient getMobileClient() {
		if(mClient == null){
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
			}

			// Default is REAL (int 0)
			int mode = getPrefHelper().getInt(ItConstant.DEVELOP_MODE_KEY);
			mClient = mode == REAL ? realClient : testClient;
		}
		return mClient;
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
	public DeviceHelper getDeviceHelper() {
		if(deviceHelper == null) deviceHelper = new DeviceHelper(app);
		return deviceHelper;
	}
	public BlobStorageHelper getBlobStorageHelper() {
		if(blobStorageHelper == null) blobStorageHelper = new BlobStorageHelper(app);
		return blobStorageHelper;
	}

	public static boolean isDebugging() {
		return app.getPrefHelper().getInt(ItConstant.DEVELOP_MODE_KEY) == ItApplication.TEST;
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

	public boolean isAdmin() {
		ItUser user = getObjectPrefHelper().get(ItUser.class);
		if (user == null) return false;

		if (adminList == null) {
			adminList = new ArrayList<String>(){
				private static final long serialVersionUID = 1L;
				{
					add("873390002701821"); // SeungMin - Facebook
					add("13276876"); // Seungmin - Kakao
					add("834118693318943"); // ChaeSoo - Facebook
					add("677830442331776"); // HongKun - Facebook
					add("13108175"); // HongKun - Kakao
					add("756536111102631"); // HwaJeong - Facebook
					add("1536364146612739"); // Item@pinthecloud.com - Facebook
				}
			};
		}
		return adminList.contains(user.getItUserId());
	}

	public void switchClient(int developMode, final EntityCallback<Boolean> callback) {
		getPrefHelper().put(ItConstant.DEVELOP_MODE_KEY, developMode);
		mClient = developMode == REAL ? realClient : testClient;
		getAimHelper().setMobileClient(mClient);
		getUserHelper().setMobileClient(mClient);
		getDeviceHelper().setMobileClient(mClient);

		ItUser user = getObjectPrefHelper().get(ItUser.class);
		ItDevice device = getObjectPrefHelper().get(ItDevice.class);
		getUserHelper().signin(user, device, new PairEntityCallback<ItUser, ItDevice>() {

			@Override
			public void onCompleted(ItUser user, ItDevice device) {
				getObjectPrefHelper().put(user);
				getObjectPrefHelper().put(device);
				callback.onCompleted(true);
			}
		});
	}
}
