package com.infinity.server.models;

public class CommonMessModel {

	private String status;

	private String message;

	String getStatus() {
		return status;
	}

	void setStatus(String status) {
		this.status = status;
	}

	String getMessage() {
		return message;
	}

	void setMessage(String message) {
		this.message = message;
	}

	public CommonMessModel(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

}
