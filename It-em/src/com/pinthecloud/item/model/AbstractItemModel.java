package com.pinthecloud.item.model;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.util.RandomUtil;

public class AbstractItemModel<T> {
	protected String id;
	protected String content;
	protected String whoMade;
	protected String whoMadeId;
	protected String rawCreateDateTime;
	protected String refId;

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
	public String getWhoMadeId() {
		return whoMadeId;
	}
	public void setWhoMadeId(String whoMadeId) {
		this.whoMadeId = whoMadeId;
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

	public boolean checkIsMine(){
		return ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class).getId().equals(this.whoMadeId);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public JsonElement toJson() {
		String jsonStr = new GsonBuilder().registerTypeAdapter(this.getClass(), new AbstractItemModelAdapter()).create().toJson(this);
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
		this.setContent(RandomUtil.getObjName() + " is a " + RandomUtil.getObjName());
		this.setWhoMade(RandomUtil.getName());
		this.setWhoMade(RandomUtil.getString());
		this.setRawCreateDateTime(ItDateTime.getToday().toString());
		this.setRefId(RandomUtil.getString(10));
		return (T)this;
	}

	private class AbstractItemModelAdapter implements JsonSerializer<T> {

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
