package com.infinity.server.models;

import java.util.ArrayList;
import java.util.List;

public class SearchMessModel {

	private String status;
	
	private String message;
	
	private List<SharedFileModel> payload = new ArrayList<SharedFileModel>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<SharedFileModel> getPayload() {
		return payload;
	}
	public void setPayload(List<SharedFileModel> payload) {
		this.payload = payload;
	}
	public void addSharedFile(SharedFileModel sharedFile) {
		payload.add(sharedFile);
	}
	
	
}