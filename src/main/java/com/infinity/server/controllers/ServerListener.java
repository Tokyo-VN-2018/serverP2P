package com.infinity.server.controllers;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerListener {

	private static ServerSocket serverSocket;

	private static final int PORT = 7777;

	public ServerListener() throws IOException {
		if (serverSocket == null) {
			serverSocket = new ServerSocket(PORT);
		}
	}

	public static void Listener() throws IOException {

		MongoController.create();
		MongoController.collection.drop();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server off");
				MongoController.collection.drop();
			}
		});

		while (true) {
			ThreadController sessionSocket = new ThreadController();
			sessionSocket.setSocket(serverSocket.accept());
			sessionSocket.start();
		}

	}

}
