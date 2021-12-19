package com.infinity.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.server.Controllers.ServerController;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Application {

	public static DBCollection collection = null;

	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			ServerController listener = SocketListener.getInstance();
			MongoClient mongodb = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
			DB db = mongodb.getDB("sharesData");
			collection = db.getCollection("listFile");
			new ServerController().accept();
		} catch (Exception e) {
			LOGGER.warn(e);
		}
	}

}
