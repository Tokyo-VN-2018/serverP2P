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
			JSONObject messObject = ackMessJsonObject.getJSONObject("payload");
			if (messObject.getString("status").equals("CONNECT")) {
				
			} else if (messObject.getString("status").equals("SEARCH")) {
				
			} else { //Them else if TH khac
				System.out.println("ERROR");
				throw new Exception("ERROR");
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
