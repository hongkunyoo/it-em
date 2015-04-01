package com.pinthecloud.item.helper;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItDevice;

import de.greenrobot.event.EventBus;

public class DeviceHelper {

	private ItApplication mApp;
	private MobileServiceClient mClient;
	private MobileServiceTable<ItDevice> deviceTable;


	public DeviceHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.deviceTable = mClient.getTable(ItDevice.class);
	}


	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
		this.deviceTable = mClient.getTable(ItDevice.class);
	}


	public void getByMobileId(String mobileId, final EntityCallback<ItDevice> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("getByMobileId", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		deviceTable.where().field("mobileId").eq(mobileId).execute(new TableQueryCallback<ItDevice>() {

			@Override
			public void onCompleted(List<ItDevice> entity, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(entity.size() == 0){
						callback.onCompleted(null);
					} else {
						callback.onCompleted(entity.get(0));
					}
				} else {
					EventBus.getDefault().post(new ItException("getByMobileId", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}
	
	
	public void del(ItDevice device, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("del", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		deviceTable.delete(device, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					EventBus.getDefault().post(new ItException("del", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}


	public void getRegistrationIdAsync(Context context, final EntityCallback<String> callback) {
		if (!mApp.isOnline()) {
			EventBus.getDefault().post(new ItException("getRegistrationId", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<GoogleCloudMessaging, Void, String>() {

			@Override
			protected String doInBackground(GoogleCloudMessaging... params) {
				GoogleCloudMessaging gcm = params[0];
				try {
					return gcm.register(mApp.getResources().getString(R.string.gcm_sender_id));
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(GoogleCloudMessaging.getInstance(context));
	}
}
