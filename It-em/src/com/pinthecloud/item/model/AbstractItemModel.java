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
	private String rawCreateDateTime;
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
	public String getRawCreateDateTime() {
		return rawCreateDateTime;
	}
	public void setRawCreateDateTime(String createTime) {
		this.rawCreateDateTime = createTime;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}

	public ItDateTime getCreateDateTime() {
		return new ItDateTime(this.rawCreateDateTime);
	}
	public void setCreateDateTime(ItDateTime dateTime) {
		this.rawCreateDateTime = dateTime.toString();
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public JsonElement toJson() {
		String jsonStr = new GsonBuilder().registerTypeAdapter(this.getClass(), new AbstractFeedModelAdapter()).create().toJson(this);
		return new Gson().fromJson(jsonStr, JsonElement.class);
	}
	
	public T rand() {
		return this.rand(false);
	}
	@SuppressWarnings("unchecked")
	public T rand(boolean hasId) {
		if (hasId) {
			this.setId(RandomUtil.getString());
		}
		this.setContent(RandomUtil.getObjName());
		this.setWhoMade(RandomUtil.getName());
		this.setRawCreateDateTime(RandomUtil.getDateTime());
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
