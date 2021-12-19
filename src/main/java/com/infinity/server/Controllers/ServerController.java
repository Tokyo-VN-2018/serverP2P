package com.infinity.server.Controllers;

import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerController {

	private ServerSocket ServerSocket;

	private static final int PORT = 7777;

	private static final Logger LOGGER = LogManager.getLogger(ServerController.class);

	public static ServerController getInstance() {
		return INSTANCE;
	}

	public ServerController() {
		// TODO Auto-generated constructor stub
	}

	public void accept() throws Exception {

		ServerSocket listener = new ServerSocket(PORT);
		try {
			// Listen to incoming sockets
			while (true) {
				new SessionController(listener.accept()).start();
			}
		} finally {
			listener.close();
		}

	}

	private static final ServerController INSTANCE = new ServerController();

}
