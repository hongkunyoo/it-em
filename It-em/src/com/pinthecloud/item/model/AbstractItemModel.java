package com.pinthecloud.item.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pinthecloud.item.ItApplication;

public class AbstractItemModel<T> {

	protected String id;
	protected String rawCreateDateTime;
	protected String content;
	protected String whoMade;
	protected String whoMadeId;
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

	public boolean checkMine(){
		return ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class).getId().equals(this.whoMadeId);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public JsonElement toJson() {
		Gson gson = new Gson();
		JsonObject json = gson.fromJson(gson.toJson(this), JsonObject.class);
		JsonObject jo = new JsonObject();
		jo.addProperty("table", getClass().getSimpleName());
		jo.add("data", json);
		return jo;
	}
}
