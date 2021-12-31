package com.infinity.server.controllers;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infinity.server.models.CommonMessModel;
import com.infinity.server.models.SharedFileModel;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

class Service {

	private String sessionId;

	private String clientIpAddress;

	private String commandPort;

	private PrintWriter outputStreamWriter;

	private JsonObject jsonObject;

	private List<Document> fileList = new ArrayList<Document>();

	private Gson gson;

	void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	void setClientIpAddress(String clientIpAddress) {
		this.clientIpAddress = clientIpAddress;
	}

	void setCommandPort(String commandPort) {
		this.commandPort = commandPort;
	}

	String getSessionId() {
		return sessionId;
	}

	String getClientIpAddress() {
		return clientIpAddress;
	}

	String getCommandPort() {
		return commandPort;
	}

	void setOutputStreamWriter(PrintWriter outputStreamWriter) {
		this.outputStreamWriter = outputStreamWriter;
	}

	void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	void setGson(Gson gson) {
		this.gson = gson;
	}

	void connectHandler() {
		SharedFileModel v1 = new SharedFileModel();
		JsonArray jsonArray = (JsonArray) jsonObject.get("payload");
		for (JsonElement i : jsonArray) {
			v1 = gson.fromJson(i, SharedFileModel.class);
			fileList.add(new Document().append("fileName", v1.getFileName()).append("filePath", v1.getFilePath())
					.append("sharer", v1.getSharer()).append("checksum", v1.getChecksum()).append("size", v1.getSize())
					.append("clientIpAddress", clientIpAddress).append("commandPort", commandPort)
					.append("sessionId", sessionId));
		}
		if (fileList.size() > 0) {
			MongoController.collection.insertMany(fileList);
			fileList.clear();
		}
		CommonMessModel mess = new CommonMessModel("CONNECT", "ACCEPT");
		outputStreamWriter.println(gson.toJson(mess));
	}

	void searchHandler() {
		String search = jsonObject.get("payload").getAsString();
		MongoCursor<Document> cursor = MongoController.collection
				.find(and(Filters.regex("fileName", search), Filters.ne("sessionId", sessionId))).iterator();
		JsonArray elements = new JsonArray();
		while (cursor.hasNext()) {
			Document jsonDocument = cursor.next();
			jsonDocument.remove("_id");
			jsonDocument.remove("clientIpAddress");
			jsonDocument.remove("commandPort");
			jsonDocument.remove("sessionId");
			elements.add(gson.toJsonTree(jsonDocument));
		}
		JsonObject messObject = new JsonObject();
		messObject.addProperty("status", "SEARCH");
		if (elements.size() > 0) {
			messObject.addProperty("message", "SUCCESS");
		} else {
			messObject.addProperty("message", "ERROR");
		}
		messObject.add("payload", elements);
		System.out.println(messObject.toString());
		outputStreamWriter.println(messObject.toString());
	}

	void infoReqHandler() {
		JsonObject fileObject = (JsonObject) jsonObject.get("payload");
		SharedFileModel v1 = new SharedFileModel();
		v1 = gson.fromJson(fileObject, SharedFileModel.class);
		Bson query = and(eq("fileName", v1.getFileName()), eq("filePath", v1.getFilePath()),
				eq("sharer", v1.getSharer()), eq("checksum", v1.getChecksum()), eq("size", v1.getSize()));
		MongoCursor<Document> cursor = MongoController.collection.find(query).iterator();
		JsonObject messObject = new JsonObject();
		messObject.addProperty("status", "INFOREQUEST");
		JsonObject payload = new JsonObject();
		if (cursor.hasNext()) {
			while (cursor.hasNext()) {
				Document jsonDocument = cursor.next();
				payload.addProperty("ip", jsonDocument.get("clientIpAddress").toString());
				payload.addProperty("commandPort", jsonDocument.get("commandPort").toString());
				break;
			}
		} else {
			payload.addProperty("ip", "N/a");
			payload.addProperty("commandPort", "-1");
		}
		messObject.add("payload", payload);
		outputStreamWriter.println(messObject.toString());
	}

	void publishHandler() {
		SharedFileModel v1 = new SharedFileModel();
		JsonArray jsonArray = (JsonArray) jsonObject.get("payload");
		for (JsonElement i : jsonArray) {
			v1 = gson.fromJson(i, SharedFileModel.class);
			fileList.add(new Document().append("fileName", v1.getFileName()).append("filePath", v1.getFilePath())
					.append("sharer", v1.getSharer()).append("checksum", v1.getChecksum()).append("size", v1.getSize())
					.append("clientIpAddress", clientIpAddress).append("commandPort", commandPort)
					.append("sessionId", sessionId));
		}
		if (fileList.size() > 0) {
			MongoController.collection.insertMany(fileList);
			fileList.clear();
		}
		CommonMessModel mess = new CommonMessModel("PUBLISH", "SUCCESS");
		outputStreamWriter.println(gson.toJson(mess));
	}

	void unPublishHandler() {
		SharedFileModel v1 = new SharedFileModel();
		JsonArray jsonArray = (JsonArray) jsonObject.get("payload");
		for (JsonElement i : jsonArray) {
			v1 = gson.fromJson(i, SharedFileModel.class);
			Bson query = and(eq("fileName", v1.getFileName()), eq("filePath", v1.getFilePath()),
					eq("sharer", v1.getSharer()), eq("checksum", v1.getChecksum()), eq("size", v1.getSize()),
					eq("sessionId", sessionId));
			MongoController.collection.deleteMany(query);
		}
		CommonMessModel mess = new CommonMessModel("UNPUBLISH", "SUCCESS");
		outputStreamWriter.println(gson.toJson(mess));
	}

	void errorHandler() {
		JsonObject fileObject = (JsonObject) jsonObject.get("payload");
		SharedFileModel v1 = new SharedFileModel();
		v1 = gson.fromJson(fileObject, SharedFileModel.class);
		Bson query = and(eq("fileName", v1.getFileName()), eq("filePath", v1.getFilePath()),
				eq("sharer", v1.getSharer()), eq("checksum", v1.getChecksum()), eq("size", v1.getSize()));
		MongoCursor<Document> cursor = MongoController.collection.find(query).iterator();
		if (cursor.hasNext()) {
			Document jsonDocument = cursor.next();
			int count = 1;
			if (jsonDocument.get("errorCount") != null) {
				count = Integer.parseInt(jsonDocument.get("errorCount").toString());
				if (count > 2) {
					MongoController.collection.deleteMany(query);
					return;
				}
				count++;
			}
			jsonDocument.append("errorCount", count);
			MongoController.collection.replaceOne(query, jsonDocument);
		}
	}

	void closeHandler() {
		Bson query = eq("sessionId", sessionId);
		MongoController.collection.deleteMany(query);
	}

}