package com.pinthecloud.item.helper;

import java.util.List;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ItPairEntityCallback;
import com.pinthecloud.item.model.ItUser;

public class UserHelper {

	private MobileServiceClient mClient;
	private MobileServiceTable<ItUser> table;


	public UserHelper(ItApplication context) {
		this.mClient = context.getMobileClient();
		this.table = mClient.getTable(ItUser.class);
	}


	public void add(final ItFragment frag, ItUser user, final ItPairEntityCallback<ItUser, Exception> callback) {
		table.insert(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity, exception);
				} else if(response.getStatus().getStatusCode() == 403) {
					callback.onCompleted((ItUser)new Gson().fromJson(response.getContent(), ItUser.class), exception);
				}else {
					ExceptionManager.fireException(new ItException(frag, "add", ItException.TYPE.SERVER_ERROR, exception));
				}
			}
		});
	}


	public void get(final ItFragment frag, String id, final ItEntityCallback<ItUser> callback) {
		table.where().field("id").eq(id).execute(new TableQueryCallback<ItUser>() {

			@Override
			public void onCompleted(List<ItUser> entity, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(count == 0){
						callback.onCompleted(null);	
					} else {
						callback.onCompleted(entity.get(0));	
					}
				} else {
					ExceptionManager.fireException(new ItException(frag, "get", ItException.TYPE.SERVER_ERROR, exception));	
				}
			}
		});
	}


	public void update(final ItFragment frag, ItUser user, final ItEntityCallback<ItUser> callback) {
		table.update(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
				} else {
					ExceptionManager.fireException(new ItException(frag, "update", ItException.TYPE.SERVER_ERROR, exception));
				}
			}
		});
	}
}
