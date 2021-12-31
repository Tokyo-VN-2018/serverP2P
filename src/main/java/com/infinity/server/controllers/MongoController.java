package com.infinity.server.controllers;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

class MongoController {

	static MongoCollection<Document> collection;

	static void create() {
		String uri = "mongodb://localhost:27017";
		MongoClient mongoClient = MongoClients.create(uri);
		MongoDatabase database = mongoClient.getDatabase("ServerData");
		collection = database.getCollection("fileList");
	}

}
