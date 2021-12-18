package com.infinity.server.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SessionController extends Thread {

	private Socket socket;

	private BufferedReader inputStreamReader;

	private PrintWriter outputStreamWriter;

	public SessionController() {
		// TODO Auto-generated constructor stub
	}

	public SessionController(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}

	public void run() {
		try {
			this.inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
			String ackMessage = inputStreamReader.readLine();
			JSONObject ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
			String status = ackMessJsonObject.getString("status");

			if (status.equals("CONNECT")) {

			} else if (status.equals("SEARCH")) {
				
			} else if (status.equals("PUBLISH")) {
				
			} else if (status.equals("UNPUBLISH")) {
				
			} else if (status.equals("INFOREQUEST")) {
				
			} else if (status.equals("QUIT")) {
				
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			closeSocket();
		}
	}

	private void closeSocket() {
		if (socket == null) {
			return;
		}

		try {
			socket.close();
			System.out.println("Socket has closed for " + socket);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
