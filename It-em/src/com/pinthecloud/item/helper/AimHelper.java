package com.pinthecloud.item.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.AbstractItemModel;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.ItLog;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class AimHelper {

	private final String AIM_ADD = "aim_add";
	private final String AIM_ADD_UNIQUE = "aim_add_unique";
	private final String AIM_UPDATE = "aim_update";
	private final String AIM_DELETE = "aim_delete";	
	private final String AIM_DELETE_ITEM = "aim_delete_item";
	private final String AIM_LIST = "aim_list";
	private final String AIM_LIST_RECENT = "aim_list_recent";
	private final String AIM_LIST_ITEM = "aim_list_item";
	private final String AIM_LIST_MY_ITEM = "aim_list_my_item";
	private final String AIM_LIST_IT_ITEM = "aim_list_it_item";
	private final String IS_VALID = "is_valid";


	private ItApplication mApp;
	private MobileServiceClient mClient;
	private BlobStorageHelper mBlobStorageHelper;


	public AimHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.mBlobStorageHelper = app.getBlobStorageHelper();
	}


	public<E extends AbstractItemModel<E>> void listItem(int page, String userId, final ListCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listMyItem", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("page", page);
		jo.addProperty("userId", userId);

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
			EventBus.getDefault().post(new ItException("listMyItem", ItException.TYPE.NETWORK_UNAVAILABLE));
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
			EventBus.getDefault().post(new ItException("listItItem", ItException.TYPE.NETWORK_UNAVAILABLE));
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
			EventBus.getDefault().post(new ItException("list", ItException.TYPE.NETWORK_UNAVAILABLE));
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
			EventBus.getDefault().post(new ItException("listRecent", ItException.TYPE.NETWORK_UNAVAILABLE));
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
						JsonObject json = _json.getAsJsonObject();

						String count = json.get("count").getAsString();
						JsonArray jsonList = json.get("list").getAsJsonArray();
						
						List<E> list = new ArrayList<E>();
						for (int i = 0 ; i < jsonList.size() ; i++) {
							list.add((E)new Gson().fromJson(jsonList.get(i), obj.getClass()));
						}

						callback.onCompleted(list, Integer.parseInt(count));
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
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.NETWORK_UNAVAILABLE));
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


	public <E extends AbstractItemModel<E>> void addUnique(final E obj, final EntityCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("addUnique", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		mClient.invokeApi(AIM_ADD_UNIQUE, obj.toJson(), new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(callback != null){
						callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));	
					}
				} else {
					EventBus.getDefault().post(new ItException("addUnique", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void del(E obj, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("del", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		mClient.invokeApi(AIM_DELETE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());
				} else {
					JsonObject responseJson = new JsonParser().parse(response.getContent()).getAsJsonObject();
					JsonElement codeJson = responseJson.get("0");
					if(codeJson != null){
						String code = codeJson.getAsJsonObject().get("code").toString();
						if(code.equals(ItException.ITEM_NOT_FOUND)){
							callback.onCompleted(_json.getAsBoolean());		
							return;
						}
					}

					EventBus.getDefault().post(new ItException("del", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}


	public void delItem(ItFragment frag, final Item item, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("delItem", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		AsyncChainer.asyncChain(frag, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				AsyncChainer.waitChain(4);

				mClient.invokeApi(AIM_DELETE_ITEM, item.toJson(), new ApiJsonOperationCallback() {

					@Override
					public void onCompleted(JsonElement _json, Exception exception,
							ServiceFilterResponse response) {
						if (exception == null) {
							AsyncChainer.notifyNext(frag, _json.getAsBoolean());
						} else {
							EventBus.getDefault().post(new ItException("delItem", ItException.TYPE.SERVER_ERROR, response));
						}
					}
				});

				mBlobStorageHelper.deleteBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId(), new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag, entity);
					}
				});

				mBlobStorageHelper.deleteBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId()+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag, entity);	
					}
				});

				mBlobStorageHelper.deleteBitmapAsync(BlobStorageHelper.ITEM_IMAGE, item.getId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag, entity);
					}
				});
			}

		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				boolean result = (Boolean)params[0];
				callback.onCompleted(result);	
			}
		});
	}


	public <E extends AbstractItemModel<E>> void update(E obj, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("update", ItException.TYPE.NETWORK_UNAVAILABLE));
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

	public void isValid(String key, ItUser.TYPE type, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("isValid", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject json = new JsonObject();
		json.addProperty("key", key);
		json.addProperty("validType", type.toString());

		mClient.invokeApi(IS_VALID, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(_json.getAsBoolean());	
				} else {
					EventBus.getDefault().post(new ItException("isValid", ItException.TYPE.SERVER_ERROR, response));
				}
			}
		});
	}
}
