package com.pinthecloud.item.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.AbstractItemModel;
import com.pinthecloud.item.model.Item;

import de.greenrobot.event.EventBus;

public class AimHelper {

	private final String AIM_GET = "aim_get";
	private final String AIM_ADD = "aim_add";
	private final String AIM_UPDATE = "aim_update";
	private final String AIM_DELETE = "aim_delete";
	private final String AIM_DELETE_ITEM = "aim_delete_item";
	private final String AIM_LIST = "aim_list";
	private final String AIM_LIST_RECENT = "aim_list_recent";
	private final String AIM_LIST_ITEM = "aim_list_item";
	private final String AIM_LIST_MY_ITEM = "aim_list_my_item";
	private final String AIM_LIST_IT_ITEM = "aim_list_it_item";

	private ItApplication mApp;
	private MobileServiceClient mClient;


	public AimHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
	}


	public<E extends AbstractItemModel<E>> void listItem(int page, final ListCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listMyItem", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("page", page);

		mClient.invokeApi(AIM_LIST_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					EventBus.getDefault().post(new ItException("listItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public void listMyItem(String userId, final ListCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listMyItem", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("userId", userId);

		mClient.invokeApi(AIM_LIST_MY_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					EventBus.getDefault().post(new ItException("listMyItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public void listItItem(String userId, final ListCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listItItem", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("userId", userId);

		mClient.invokeApi(AIM_LIST_IT_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					EventBus.getDefault().post(new ItException("listMyUploadItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public<E extends AbstractItemModel<E>> void list(Class<E> clazz, String itemId, final ListCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("list", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		try {
			final E obj = clazz.newInstance();
			JsonObject jo = new JsonObject();
			jo.addProperty("table", obj.getClass().getSimpleName());
			jo.addProperty("refId", itemId);

			mClient.invokeApi(AIM_LIST, jo, new ApiJsonOperationCallback() {

				@SuppressWarnings("unchecked")
				@Override
				public void onCompleted(JsonElement _json, Exception exception,
						ServiceFilterResponse response) {
					if(exception == null){
						JsonArray arr = _json.getAsJsonArray();
						List<E> list = new ArrayList<E>();
						for (int i = 0 ; i < arr.size() ; i++) {
							list.add((E)new Gson().fromJson(arr.get(i), obj.getClass()));
						}
						callback.onCompleted(list, list.size());	
					} else {
						EventBus.getDefault().post(new ItException("list", ItException.TYPE.SERVER_ERROR, response));
					}
				}
			});
		} catch (InstantiationException e) {
			EventBus.getDefault().post(new ItException("list", ItException.TYPE.INTERNAL_ERROR));
		} catch (IllegalAccessException e) {
			EventBus.getDefault().post(new ItException("list", ItException.TYPE.INTERNAL_ERROR));
		}
	}


	public<E extends AbstractItemModel<E>> void listRecent(Class<E> clazz, String itemId, final ListCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listRecent", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		try {
			final E obj = clazz.newInstance();
			JsonObject jo = new JsonObject();
			jo.addProperty("table", obj.getClass().getSimpleName());
			jo.addProperty("refId", itemId);

			mClient.invokeApi(AIM_LIST_RECENT, jo, new ApiJsonOperationCallback() {

				@SuppressWarnings("unchecked")
				@Override
				public void onCompleted(JsonElement _json, Exception exception,
						ServiceFilterResponse response) {
					if(exception == null){
						JsonArray arr = _json.getAsJsonArray();
						List<E> list = new ArrayList<E>();
						for (int i = 0 ; i < arr.size() ; i++) {
							list.add((E)new Gson().fromJson(arr.get(i), obj.getClass()));
						}
						callback.onCompleted(list, list.size());
					} else {
						EventBus.getDefault().post(new ItException("listRecent", ItException.TYPE.SERVER_ERROR, response));
					}
				}
			});
		} catch (InstantiationException e) {
			EventBus.getDefault().post(new ItException("listRecent", ItException.TYPE.INTERNAL_ERROR));
		} catch (IllegalAccessException e) {
			EventBus.getDefault().post(new ItException("listRecent", ItException.TYPE.INTERNAL_ERROR));
		}
	}


	public <E extends AbstractItemModel<E>> void add(final E obj, final EntityCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		mClient.invokeApi(AIM_ADD, obj.toJson(), new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(callback != null){
						callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));	
					}
				} else {
					EventBus.getDefault().post(new ItException("add", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void del(E obj, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("del", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		mClient.invokeApi(AIM_DELETE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());	
				} else {
					EventBus.getDefault().post(new ItException("del", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public void delItem(Item obj, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("delItem", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		mClient.invokeApi(AIM_DELETE_ITEM, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());	
				} else {
					EventBus.getDefault().post(new ItException("delItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void get(final E obj, final EntityCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("get", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		mClient.invokeApi(AIM_GET, obj.toJson(), new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));	
				} else {
					EventBus.getDefault().post(new ItException("get", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void update(E obj, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("update", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		mClient.invokeApi(AIM_UPDATE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());	
				} else {
					EventBus.getDefault().post(new ItException("update", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}
}
