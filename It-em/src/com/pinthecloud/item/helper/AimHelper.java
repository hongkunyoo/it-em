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
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.AbstractItemModel;
import com.pinthecloud.item.model.ItDateTime;
import com.pinthecloud.item.model.Item;

public class AimHelper {

	private static final String RANK_ITEM = "rank_10";
	private final String AIM_LIST = "aim_list";
	private final String AIM_ITEM_LIST = "aim_item_list";
	private final String AIM_GET = "aim_get";
	private final String AIM_ADD = "aim_add";
	private final String AIM_UPDATE = "aim_update";
	private final String AIM_DELETE = "aim_delete";
	private final String MY_UPLOAD_ITEM = "my_upload_item";
	private final String MY_IT_ITEM = "my_it_item";

	private MobileServiceClient mClient;


	public AimHelper(ItApplication context) {
		this.mClient = context.getMobileClient();
	}


	public<E extends AbstractItemModel<E>> void listItem(final ItFragment frag, int page, final ItListCallback<Item> callback) {
		JsonObject jo = new JsonObject();
		jo.addProperty("page", page);

		mClient.invokeApi(AIM_ITEM_LIST, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					ExceptionManager.fireException(new ItException(frag, "listItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}

	public void getRank10(final ItFragment frag, ItDateTime date, final ItListCallback<Item> callback) {

		JsonObject jo = new JsonObject();
		jo.addProperty("date", date.toString());
		mClient.invokeApi(RANK_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					ExceptionManager.fireException(new ItException(frag, "getRank10", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public void listMyItem(final ItFragment frag, String userId, final ItListCallback<Item> callback) {
		JsonObject jo = new JsonObject();
		jo.addProperty("userId", userId);
		mClient.invokeApi(MY_UPLOAD_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					ExceptionManager.fireException(new ItException(frag, "listMyUploadItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public void listItItem(final ItFragment frag, String userId, final ItListCallback<Item> callback) {
		JsonObject jo = new JsonObject();
		jo.addProperty("userId", userId);
		mClient.invokeApi(MY_IT_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<Item> list = new Gson().fromJson(json, new TypeToken<List<Item>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					ExceptionManager.fireException(new ItException(frag, "listMyUploadItem", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public<E extends AbstractItemModel<E>> void list(final ItFragment frag, Class<E> clazz, String itemId, final ItListCallback<E> callback) {
		if (itemId == null || itemId.equals("")) return;

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
						ExceptionManager.fireException(new ItException(frag, "list", ItException.TYPE.SERVER_ERROR, response));
					}
				}
			});
		} catch (InstantiationException e) {
			throw new ItException(ItException.TYPE.NO_SUCH_INSTANCE);
		} catch (IllegalAccessException e) {
			throw new ItException(ItException.TYPE.NO_SUCH_INSTANCE);
		}
	}


	public <E extends AbstractItemModel<E>> void add(final ItFragment frag, final E obj, final ItEntityCallback<E> callback) {
		mClient.invokeApi(AIM_ADD, obj.toJson(), new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));	
				} else {
					ExceptionManager.fireException(new ItException(frag, "add", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void del(final ItFragment frag, E obj, final ItEntityCallback<Boolean> callback) {
		mClient.invokeApi(AIM_DELETE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());	
				} else {
					ExceptionManager.fireException(new ItException(frag, "del", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void get(final ItFragment frag, final E obj, final ItEntityCallback<E> callback) {
		mClient.invokeApi(AIM_GET, obj.toJson(), new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));	
				} else {
					ExceptionManager.fireException(new ItException(frag, "get", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void update(final ItFragment frag, E obj, final ItEntityCallback<Boolean> callback) {
		mClient.invokeApi(AIM_UPDATE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());	
				} else {
					ExceptionManager.fireException(new ItException(frag, "update", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}
}
