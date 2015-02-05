package com.pinthecloud.item.helper;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpStatus;

import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class UserHelper {

	private ItApplication mApp;
	private MobileServiceClient mClient;
	private MobileServiceTable<ItUser> table;


	public UserHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.table = mClient.getTable(ItUser.class);
	}


	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
		this.table = mClient.getTable(ItUser.class);
	}


	public void add(ItUser user, final PairEntityCallback<ItUser, Exception> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		table.insert(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity, exception);
				} else if(response.getStatus().getStatusCode() == HttpStatus.SC_FORBIDDEN) {	// Duplicate user
					ItUser itUser = (ItUser) new Gson().fromJson(response.getContent(), ItUser.class);
					callback.onCompleted(itUser, exception);
				} else {
					EventBus.getDefault().post(new ItException("add", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}


	public void get(String id, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("get", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		table.where().field("id").eq(id).execute(new TableQueryCallback<ItUser>() {

			@Override
			public void onCompleted(List<ItUser> entity, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(entity.size() == 0){
						callback.onCompleted(null);	
					} else {
						callback.onCompleted(entity.get(0));	
					}
				} else {
					EventBus.getDefault().post(new ItException("get", ItException.TYPE.INTERNAL_ERROR, exception));	
				}
			}
		});
	}


	public void getByNickName(String nickName, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("getByNickName", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		table.where().field("nickName").eq(nickName).execute(new TableQueryCallback<ItUser>() {

			@Override
			public void onCompleted(List<ItUser> entity, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(entity.size() == 0){
						callback.onCompleted(null);
					} else {
						callback.onCompleted(entity.get(0));
					}
				} else {
					EventBus.getDefault().post(new ItException("getByNickName", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}

	public void getByItUserId(String itUserId, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("getByItUserId", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		table.where().field("itUserId").eq(itUserId).execute(new TableQueryCallback<ItUser>() {

			@Override
			public void onCompleted(List<ItUser> entity, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(entity.size() == 0){
						callback.onCompleted(null);
					} else {
						callback.onCompleted(entity.get(0));
					}
				} else {
					EventBus.getDefault().post(new ItException("getByItUserId", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}


	public void update(ItUser user, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("update", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		table.update(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
				} else {
					EventBus.getDefault().post(new ItException("update", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}


	//	public void getRegistrationIdAsync(final ItFragment frag, final EntityCallback<String> callback) {
	//		if (!mApp.isOnline()) {
	//			EventBus.getDefault().post(new ItException("getRegistrationIdAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
	//			return;
	//		}
	//
	//		(new AsyncTask<GoogleCloudMessaging, Void, String>() {
	//
	//			@Override
	//			protected String doInBackground(GoogleCloudMessaging... params) {
	//				GoogleCloudMessaging gcm = params[0];
	//				try {
	//					return gcm.register(GlobalVariable.GCM_SENDER_ID);
	//				} catch (IOException e) {
	//					return null;
	//				}
	//			}
	//
	//			@Override
	//			protected void onPostExecute(String result) {
	//				super.onPostExecute(result);
	//				if (result != null) {
	//					callback.onCompleted(result);
	//				} else {
	//					EventBus.getDefault().post(new ItException("getRegistrationIdAsync", ItException.TYPE.GCM_REGISTRATION_FAIL));
	//				}
	//			}
	//		}).execute(GoogleCloudMessaging.getInstance(frag.getActivity()));
	//	}


	public void getRegistrationId(ItActivity activity, final EntityCallback<String> callback) {
		if (!mApp.isOnline()) {
			EventBus.getDefault().post(new ItException("getRegistrationId", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<GoogleCloudMessaging, Void, String>() {

			@Override
			protected String doInBackground(GoogleCloudMessaging... params) {
				GoogleCloudMessaging gcm = params[0];
				try {
					return gcm.register(GlobalVariable.GCM_SENDER_ID);
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(GoogleCloudMessaging.getInstance(activity));
	}
}
