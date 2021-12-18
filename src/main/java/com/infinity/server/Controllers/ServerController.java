package com.infinity.server.Controllers;

import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerController {

	private ServerSocket ServerSocket;

	private static final int PORT = 7777;
	
	private static final Logger LOGGER = LogManager.getLogger(ServerController.class);

	public ServerController() {
		// TODO Auto-generated constructor stub
	}

	public void accept() throws Exception {

		this.ServerSocket = new ServerSocket(PORT);

		try {
			while (true) {
				LOGGER.info("Client connect");
				SessionController session = new SessionController(this.ServerSocket.accept());
			}
		} finally {
			this.ServerSocket.close();
		}
	}

}
