package com.pinthecloud.item.helper;

import android.content.pm.PackageManager.NameNotFoundException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.AppVersion;

import de.greenrobot.event.EventBus;

public class VersionHelper {

	private String GET_APP_VERSION = "get_app_version";
	private MobileServiceClient mClient;
	public enum TYPE {
		MANDATORY,
		OPTIONAL
	}

	private ItApplication app;
//	private MobileServiceTable<AppVersion> appVersionTable;


	public VersionHelper() {
		app = ItApplication.getInstance();
	}


//	public void insertAppVersionAsync(final AhFragment frag, AppVersion appVersion, final AhEntityCallback<AppVersion> callback) throws AhException {
//		if (!app.isOnline()) {
//			ExceptionManager.fireException(new AhException(frag, "createSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
//			return;
//		}
//
//		appVersionTable.insert(appVersion, new TableOperationCallback<AppVersion>() {
//
//			@Override
//			public void onCompleted(AppVersion entity, Exception exception, ServiceFilterResponse response) {
//				if (exception == null) {
//					if (callback != null){
//						callback.onCompleted(entity);
//					}
//					AsyncChainer.notifyNext(frag);
//				} else {
//					ExceptionManager.fireException(new AhException(frag, "createSquareAsync", AhException.TYPE.SERVER_ERROR));
//				}
//			}
//		});
//	}


	public void getServerAppVersionAsync(final EntityCallback<AppVersion> callback) {
		if (!app.isOnline()) {
			EventBus.getDefault().post(new ItException("getServerAppVersionAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}
		
		mClient.invokeApi(GET_APP_VERSION, new ApiJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					AppVersion version = new Gson().fromJson(arg0, AppVersion.class);
					callback.onCompleted(version);
				} else {
					arg1.printStackTrace();
					EventBus.getDefault().post(new ItException("getServerAppVersionAsync", ItException.TYPE.SERVER_ERROR, arg1));
				}
				
			}
		});
		
	}


	public double getClientAppVersion() throws NameNotFoundException {
		String versionName = app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName;
		return Double.parseDouble(versionName);
	}

}


// get Version from server;
// get Version from client;
// compare
// if (isOld)
//		show Dialog;
// else
//		keep do afterward thing;

//
//new AlertDialog.Builder(Test.this)
//.setIcon(R.drawable.icon)
//.setTitle("Update Available")
//.setMessage("An update for is available!\\n\\nOpen Android Market and see the details?")
//.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//        public void onClick(DialogInterface dialog, int whichButton) {
//                /* User clicked OK so do some stuff */
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:your.app.id"));
//                startActivity(intent);
//        }
//})
//.setNegativeButton("No", new DialogInterface.OnClickListener() {
//        public void onClick(DialogInterface dialog, int whichButton) {
//                /* User clicked Cancel */
//        }
//})
//.show();
//}