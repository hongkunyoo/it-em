package com.pinthecloud.item.helper;

import java.util.List;

import com.google.gson.Gson;
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
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.MyLog;

public class AimHelper {

	private MobileServiceClient mClient;
	private final String AIM_LIST = "aim_list";
	private final String AIM_ITEM_LIST = "aim_item_list";
	private final String AIM_GET = "aim_get";
	private final String AIM_ADD = "aim_add";
	private final String AIM_UPDATE = "aim_update";
	private final String AIM_DELETE = "aim_delete";

	public AimHelper() {
		mClient = ItApplication.getInstance().getMobileClient();
	}

	public<E extends AbstractItemModel<E>> void listItem(int page, final ItListCallback<Item> callback) {

		JsonObject jo = new JsonObject();
		jo.addProperty("page", page);

		mClient.invokeApi(AIM_ITEM_LIST, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				if (arg1 == null) {
					MyLog.log(arg0);
				} else {
					MyLog.log(arg1);
				}
			}
		});
	}

	public<E extends AbstractItemModel<E>> void list(E obj, final ItListCallback<E> callback) {
		String refId = obj.getRefId();
		if (refId == null || refId.equals("")) return;

		mClient.invokeApi(AIM_LIST, obj.toJson(), new ApiJsonOperationCallback() {

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
	
	public<E extends AbstractItemModel<E>> void list(String refId, final ItListCallback<E> callback) {
		if (refId == null || refId.equals("")) return;

		mClient.invokeApi(AIM_LIST, null, new ApiJsonOperationCallback() {

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

	public <E extends AbstractItemModel<E>> void add(final ItFragment frag, final E obj, final ItEntityCallback<String> callback) {

		mClient.invokeApi(AIM_ADD, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					callback.onCompleted(arg0.getAsString());
				} else {
					String content = null;
					if (arg2 != null) content = arg2.getContent();
					ExceptionManager.fireException(new ItException(frag, "add", ItException.TYPE.SERVER_ERROR, content));
				}
				
			}
		});
	}

	public <E extends AbstractItemModel<E>> void del(final E obj, final ItEntityCallback<Boolean> callback) {

		mClient.invokeApi(AIM_DELETE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				callback.onCompleted(arg0.getAsBoolean());
			}
		});
	}

	public <E extends AbstractItemModel<E>> void get(final E obj, final ItEntityCallback<E> callback) {

		mClient.invokeApi(AIM_GET, obj.toJson(), new ApiJsonOperationCallback() {

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

		mClient.invokeApi(AIM_UPDATE, obj.toJson(), new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				callback.onCompleted(arg0.getAsBoolean());
			}
		});
	}
}
