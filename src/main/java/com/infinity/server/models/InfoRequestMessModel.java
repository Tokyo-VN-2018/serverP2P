package com.infinity.server.models;

import com.alibaba.fastjson.JSONObject;

public class InfoRequestMessModel {
	private String status;
	
	private JSONObject payload;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public JSONObject getPayload() {
		return payload;
	}

	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}
	
}
