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

	private final String GET_APP_VERSION = "get_app_version";
	
	public enum TYPE {
		MANDATORY,
		OPTIONAL
	}
	
	private ItApplication mApp;
	private MobileServiceClient mClient;

	public VersionHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
	}
	
	public void setMobileClient(MobileServiceClient mClient) {
		this.mClient = mClient;
	}

	public void getServerAppVersionAsync(final EntityCallback<AppVersion> callback) {
		if (!mApp.isOnline()) {
			EventBus.getDefault().post(new ItException("getServerAppVersionAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}
		
		mClient.invokeApi(GET_APP_VERSION, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(new Gson().fromJson(_json, AppVersion.class));
				} else {
					EventBus.getDefault().post(new ItException("getServerAppVersionAsync", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
		
	}

	public double getClientAppVersion() {
		String versionName = "0.1";
		try {
			versionName = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return Double.parseDouble(versionName);
	}
}
