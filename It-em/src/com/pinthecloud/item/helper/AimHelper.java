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
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.AbstractItemModel;
import com.pinthecloud.item.model.HashTag;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.ImageUtil;

import de.greenrobot.event.EventBus;

public class AimHelper {

	private final String AIM_ADD = "aim_add";
	private final String AIM_ADD_UNIQUE = "aim_add_unique";
	private final String AIM_ADD_ITEM = "aim_add_item";
	private final String AIM_DELETE = "aim_delete";	
	private final String AIM_DELETE_ITEM = "aim_delete_item";
	private final String AIM_GET_ITEM = "aim_get_item";
	private final String AIM_LIST = "aim_list";
	private final String AIM_LIST_ITEM = "aim_list_item";
	private final String AIM_LIST_IT_ITEM = "aim_list_it_item";
	private final String AIM_LIST_MY_ITEM = "aim_list_my_item";
	private final String AIM_LIST_MY_NOTI = "aim_list_my_noti";

	private ItApplication mApp;
	private MobileServiceClient mClient;
	private BlobStorageHelper mBlobStorageHelper;


	public AimHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.mBlobStorageHelper = app.getBlobStorageHelper();
	}


	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
	}


	public <E extends AbstractItemModel<E>> void add(final E obj, ItNotification noti, final EntityCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.add("item", obj.toJson());
		jo.add("noti", noti.toJson());

		mClient.invokeApi(AIM_ADD, jo, new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));
				} else {
					EventBus.getDefault().post(new ItException("add", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public <E extends AbstractItemModel<E>> void addUnique(final E obj, ItNotification noti, final EntityCallback<E> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("addUnique", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.add("item", obj.toJson());
		jo.add("noti", noti.toJson());

		mClient.invokeApi(AIM_ADD_UNIQUE, jo, new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(callback != null){
						callback.onCompleted((E)new Gson().fromJson(_json, obj.getClass()));	
					}
				} else {
					EventBus.getDefault().post(new ItException("addUnique", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public void addItem(final Item item, List<HashTag> tagList, final EntityCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("addItem", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		Gson gson = new Gson();
		JsonArray tagListJson = gson.fromJson(gson.toJson(tagList), JsonArray.class);
		JsonObject jo = new JsonObject();
		jo.add("item", item.toJson());
		jo.add("tagList", tagListJson);

		mClient.invokeApi(AIM_ADD_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(new Gson().fromJson(_json, Item.class));
				} else {
					EventBus.getDefault().post(new ItException("addItem", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public void getItem(Item item, String userId, final EntityCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("getItem", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.add("item", item.toJson());
		jo.addProperty("userId", userId);

		mClient.invokeApi(AIM_GET_ITEM, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(new Gson().fromJson(_json, Item.class));	
				} else {
					EventBus.getDefault().post(new ItException("getItem", ItException.TYPE.INTERNAL_ERROR, response));
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
						EventBus.getDefault().post(new ItException("list", ItException.TYPE.INTERNAL_ERROR, response));
					}
				}
			});
		} catch (InstantiationException e) {
			EventBus.getDefault().post(new ItException("list", ItException.TYPE.INTERNAL_ERROR));
		} catch (IllegalAccessException e) {
			EventBus.getDefault().post(new ItException("list", ItException.TYPE.INTERNAL_ERROR));
		}
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
					EventBus.getDefault().post(new ItException("listMyItem", ItException.TYPE.INTERNAL_ERROR, response));
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
					EventBus.getDefault().post(new ItException("listItItem", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public<E extends AbstractItemModel<E>> void listItem(int page, String userId, final ListCallback<Item> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listItem", ItException.TYPE.NETWORK_UNAVAILABLE));
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
					EventBus.getDefault().post(new ItException("listItem", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public void listMyNoti(int page, String userId, final ListCallback<ItNotification> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("listMyNoti", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("page", page);
		jo.addProperty("userId", userId);

		mClient.invokeApi(AIM_LIST_MY_NOTI, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonElement json = _json.getAsJsonArray();
					List<ItNotification> list = new Gson().fromJson(json, new TypeToken<List<ItNotification>>(){}.getType());
					callback.onCompleted(list, list.size());
				} else {
					EventBus.getDefault().post(new ItException("listMyNoti", ItException.TYPE.INTERNAL_ERROR, response));
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
					EventBus.getDefault().post(new ItException("del", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public void deleteItem(ItFragment frag, final Item item, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("delItem", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		AsyncChainer.asyncChain(frag, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				AsyncChainer.waitChain(1 + item.getImageNumber()*2 + 1);

				mClient.invokeApi(AIM_DELETE_ITEM, item.toJson(), new ApiJsonOperationCallback() {

					@Override
					public void onCompleted(JsonElement _json, Exception exception,
							ServiceFilterResponse response) {
						if(exception == null){
							AsyncChainer.notifyNext(obj, _json.getAsBoolean());
						} else {
							EventBus.getDefault().post(new ItException("deleteItem", ItException.TYPE.INTERNAL_ERROR, exception));
						}
					}
				});

				for(int i=0 ; i<item.getImageNumber() ; i++){
					deleteItemImage(obj, item, i);
				}
			}

		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				boolean result = (Boolean)params[0];
				callback.onCompleted(result);
			}
		});
	}


	private void deleteItemImage(final Object obj, Item item, int index){
		String imageId = index == 0 ? item.getId() : item.getId() + "_" + index;
		mBlobStorageHelper.deleteBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, imageId,
				new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				AsyncChainer.notifyNext(obj, entity);
			}
		});

		mBlobStorageHelper.deleteBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, imageId+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX,
				new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				AsyncChainer.notifyNext(obj, entity);
			}
		});

		if(index == 0){
			mBlobStorageHelper.deleteBitmapAsync(BlobStorageHelper.CONTAINER_ITEM_IMAGE, imageId+ImageUtil.ITEM_PREVIEW_IMAGE_POSTFIX,
					new EntityCallback<Boolean>() {

				@Override
				public void onCompleted(Boolean entity) {
					AsyncChainer.notifyNext(obj, entity);
				}
			});
		}
	}
}
