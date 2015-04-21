package com.pinthecloud.item.helper;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
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


	public void del(ItDevice device, final EntityCallback<Integer> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("del", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		deviceTable.delete(device, new TableDeleteCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse response) {
				callback.onCompleted(response.getStatus().getStatusCode());
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
				String regId = null;
				try {
					regId = gcm.register(mApp.getResources().getString(R.string.gcm_sender_id));
				} catch (IOException e) {
					EventBus.getDefault().post(new ItException("getRegistrationIdAsync", ItException.TYPE.INTERNAL_ERROR));
				}
				return regId;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(GoogleCloudMessaging.getInstance(context));
	}
}
