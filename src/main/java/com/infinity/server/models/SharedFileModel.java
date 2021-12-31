package com.infinity.server.models;

public class SharedFileModel {

	private String fileName;

	private String filePath;

	private String checksum;

	private String size;

	public String getFileName() {
		return fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getChecksum() {
		return checksum;
	}

	public String getSize() {
		return size;
	}

//	public SharedFileModel(String fileName, String filePath, String sharer, String checksum, String size) {
//		super();
//		this.fileName = fileName;
//		this.filePath = filePath;
//		this.sharer = sharer;
//		this.checksum = checksum;
//		this.size = size;
//	}

}
