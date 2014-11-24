package com.pinthecloud.item.model;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pinthecloud.item.util.RandomUtil;

public class AbstractItemModel<T> {
	private String id;
	private String content;
	private String whoMade;
	private String createTime;
	private String refId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWhoMade() {
		return whoMade;
	}
	public void setWhoMade(String whoMade) {
		this.whoMade = whoMade;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public JsonElement toJson() {
		String jsonStr = new GsonBuilder().registerTypeAdapter(this.getClass(), new AbstractFeedModelAdapter()).create().toJson(this);
		return new Gson().fromJson(jsonStr, JsonElement.class);
	}
	
	@SuppressWarnings("unchecked")
	public T rand() {
		this.setContent(RandomUtil.getObjName());
		this.setWhoMade(RandomUtil.getName());
		this.setCreateTime(RandomUtil.getTime());
		this.setRefId(RandomUtil.getString(10));
		return (T)this;
	}
	
	private class AbstractFeedModelAdapter implements JsonSerializer<T> {

		@Override
		public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
			Gson gson = new Gson();
			JsonObject json = gson.fromJson(gson.toJson(src), JsonObject.class);
			JsonObject jo = new JsonObject();
			jo.addProperty("table", src.getClass().getSimpleName());
			jo.add("data", json);
			return jo;
		}
	}
}
