package com.infinity.server.controllers;

import java.io.IOException;
import java.net.Socket;

class ThreadController extends Thread {

	private Socket socket;

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		SessionController session = new SessionController();
		try {
			session.setSocket(socket);
			session.SessionListener();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
