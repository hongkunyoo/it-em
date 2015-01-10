package com.pinthecloud.item.helper;

import java.util.List;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
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
					EventBus.getDefault().post(new ItException("add", ItException.TYPE.SERVER_ERROR, exception));
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
					EventBus.getDefault().post(new ItException("get", ItException.TYPE.SERVER_ERROR, exception));	
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
					EventBus.getDefault().post(new ItException("getByNickName", ItException.TYPE.SERVER_ERROR, exception));
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
					EventBus.getDefault().post(new ItException("update", ItException.TYPE.SERVER_ERROR, exception));
				}
			}
		});
	}
}
