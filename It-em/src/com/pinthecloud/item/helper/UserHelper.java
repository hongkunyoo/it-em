package com.pinthecloud.item.helper;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class UserHelper {
	
	private final String SIGNIN = "signin";
	private final String UPDATE_USER = "update_user";
	private final String BE_PRO = "be_pro";
	
	private ItApplication mApp;
	private MobileServiceClient mClient;
	private MobileServiceTable<ItUser> userTable;


	public UserHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.userTable = mClient.getTable(ItUser.class);
	}


	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
		this.userTable = mClient.getTable(ItUser.class);
	}


	public void signin(ItUser user, ItDevice device, final PairEntityCallback<ItUser, ItDevice> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("signin", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("user", user.toString());
		jo.addProperty("device", device.toString());
		
		mClient.invokeApi(SIGNIN, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonObject json = _json.getAsJsonObject();
					ItUser user = new Gson().fromJson(json.get("user"), ItUser.class);
					ItDevice device = new Gson().fromJson(json.get("device"), ItDevice.class);
					callback.onCompleted(user, device);
				} else {
					EventBus.getDefault().post(new ItException("signin", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}
	
	
	public void bePro(ItUser user, String key, ItUser.TYPE type, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("bePro", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("user", user.toString());
		jo.addProperty("key", key);
		jo.addProperty("validType", type.toString());
		
		mClient.invokeApi(BE_PRO, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(new Gson().fromJson(_json, ItUser.class));
				} else {
					EventBus.getDefault().post(new ItException("bePro", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}
	
	
	public void add(ItUser user, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		userTable.insert(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
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

		userTable.where().field("id").eq(id).execute(new TableQueryCallback<ItUser>() {

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

		userTable.where().field("nickName").eq(nickName).execute(new TableQueryCallback<ItUser>() {

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

		userTable.where().field("itUserId").eq(itUserId).execute(new TableQueryCallback<ItUser>() {

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

		userTable.update(user, new TableOperationCallback<ItUser>() {

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
	
	
	public void updateUser(ItUser user, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("updateUser", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("user", user.toString());
		
		mClient.invokeApi(UPDATE_USER, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(new Gson().fromJson(_json, ItUser.class));
				} else {
					EventBus.getDefault().post(new ItException("update", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}
}
