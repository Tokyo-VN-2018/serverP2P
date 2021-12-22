package com.infinity.server.models;

public class SharedFileModelv2 extends SharedFileModel {
	private String clientIpAddress;
	private int commandPort;
	private int errorCount;
	
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public String getClientIpAddress() {
		return clientIpAddress;
	}
	public void setClientIpAddress(String clientIpAddress) {
		this.clientIpAddress = clientIpAddress;
	}
	public int getCommandPort() {
		return commandPort;
	}
	public void setCommandPort(int commandPort) {
		this.commandPort = commandPort;
	}
	
	

}
