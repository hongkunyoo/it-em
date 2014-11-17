package com.pinthecloud.item.helper;

import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.AbstractItemModel;
import com.pinthecloud.item.model.Item;

public class AimHelper {

	private MobileServiceClient mClient;
	//	private MobileServiceTable<Feed> feedTable;
	//	private MobileServiceTable<Like> likeTable;

	public AimHelper(Context context) {
		mClient = ItApplication.getMobileClient();
	}

	public<E extends AbstractItemModel<E>> void listFeed(String refId, final ItListCallback<Item> callback) {
		if (refId == null || refId.equals("")) return;

		JsonObject jo = new JsonObject();
		jo.addProperty("refId", refId);

		mClient.invokeApi("afm_list_feed", jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				//				JsonElement json = arg0.getAsJsonArray();
				//				List<E> list = new Gson().fromJson(json, new TypeToken<List<?>>(){}.getType());
				//				callback.onComplete(list);
			}
		});
	}

	public<E extends AbstractItemModel<E>> void list(E obj, final ItListCallback<E> callback) {
		String refId = obj.getRefId();
		if (refId == null || refId.equals("")) return;

		mClient.invokeApi("afm_list", obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				JsonElement json = arg0.getAsJsonArray();
				List<E> list = new Gson().fromJson(json, new TypeToken<List<?>>(){}.getType());
				callback.onCompleted(list, list.size());
			}
		});
	}

	public <E extends AbstractItemModel<E>> void add(final E obj, final ItEntityCallback<String> callback) {

		mClient.invokeApi("afm_add", obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				callback.onCompleted(arg0.getAsString());
			}
		});
	}

	public <E extends AbstractItemModel<E>> void del(final E obj, final ItEntityCallback<Boolean> callback) {

		mClient.invokeApi("afm_delete", obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				callback.onCompleted(arg0.getAsBoolean());
			}
		});
	}

	public <E extends AbstractItemModel<E>> void get(final E obj, final ItEntityCallback<E> callback) {

		mClient.invokeApi("afm_get", obj.toJson(), new ApiJsonOperationCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				callback.onCompleted((E)new Gson().fromJson(arg0, obj.getClass()));
			}
		});
	}

	public <E extends AbstractItemModel<E>> void update(final E obj, final ItEntityCallback<Boolean> callback) {

		mClient.invokeApi("afm_update", obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				callback.onCompleted(arg0.getAsBoolean());
			}
		});
	}
}
