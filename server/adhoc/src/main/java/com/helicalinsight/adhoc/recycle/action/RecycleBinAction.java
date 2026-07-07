package com.helicalinsight.adhoc.recycle.action;

import com.google.gson.JsonObject;

public abstract class RecycleBinAction {

	protected JsonObject formData;
	
	public abstract String performAction();

	public JsonObject getFormData() {
		return formData;
	}

	public void setFormData(JsonObject formData) {
		this.formData = formData;
	}
}
