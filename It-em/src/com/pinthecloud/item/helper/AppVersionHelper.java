package com.pinthecloud.item.helper;

import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.AppVersion;

import de.greenrobot.event.EventBus;

public class AppVersionHelper {

	private ItApplication mApp;
	private MobileServiceClient mClient;
	private MobileServiceTable<AppVersion> appVersionTable;


	public AppVersionHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.appVersionTable = mClient.getTable(AppVersion.class);
	}


	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
		this.appVersionTable = mClient.getTable(AppVersion.class);
	}


	public void get(String id, final EntityCallback<AppVersion> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("get", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		appVersionTable.where().execute(new TableQueryCallback<AppVersion>() {

			@Override
			public void onCompleted(List<AppVersion> appVersionList, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(appVersionList.get(0));
				} else {
					EventBus.getDefault().post(new ItException("get", ItException.TYPE.INTERNAL_ERROR, exception));	
				}
			}
		});
	}
}
