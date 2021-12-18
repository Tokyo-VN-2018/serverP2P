package com.infinity.server.Controllers;

import java.net.ServerSocket;

public class ServerController {

	private ServerSocket ServerSocket;

	private static final int PORT = 7777;

	public ServerController() {
		// TODO Auto-generated constructor stub
	}

	public void accept() throws Exception {

		this.ServerSocket = new ServerSocket(PORT);

		try {
			while (true) {
				SessionController session = new SessionController(this.ServerSocket.accept());
			}
		} finally {
			this.ServerSocket.close();
		}
	}

}
