package com.infinity.server.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infinity.server.models.CommonMessModel;

class SessionController {

	private static Gson gson = new Gson();

	private Socket socket;

	private BufferedReader inputStreamReader;

	private PrintWriter outputStreamWriter;

	private String ackMessage;

	private Service service = new Service();

	void setSocket(Socket socket) throws IOException {
		this.socket = socket;
		inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
	}

	void SessionListener() throws IOException {
		try {

			service.setOutputStreamWriter(outputStreamWriter);
			service.setSessionId(socket.getInetAddress().getHostAddress() + socket.getPort());
			service.setClientIpAddress(socket.getInetAddress().getHostAddress());
			service.setGson(gson);

			while (socket.isConnected()) {
				ackMessage = inputStreamReader.readLine();
				System.out.println(ackMessage);
				JsonObject jsonObject = JsonParser.parseString(ackMessage).getAsJsonObject();
				service.setJsonObject(jsonObject);
				String status = jsonObject.get("status").getAsString();
				try {
					if (status.equals("CONNECT")) {
						service.setUsername(jsonObject.get("username").getAsString());
						service.setCommandPort(jsonObject.get("commandPort").getAsString());
						service.connectHandler();
					} else if (status.equals("SEARCH")) {
						service.searchHandler();
					} else if (status.equals("PUBLISH")) {
						service.publishHandler();
					} else if (status.equals("UNPUBLISH")) {
						service.unPublishHandler();
					} else if (status.equals("ERRDOWNLOAD")) {
						service.errorHandler();
					} else if (status.equals("QUIT")) {
						service.closeHandler();
						socket.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
					if (status.equals("CONNECT")) {
						CommonMessModel mess = new CommonMessModel("CONNECT", "REFUSE");
						outputStreamWriter.println(gson.toJson(mess));
					} else if (status.equals("SEARCH")) {
						JsonObject messObject = new JsonObject();
						messObject.addProperty("status", "SEARCH");
						messObject.addProperty("message", "ERROR");
						messObject.add("payload", null);
						outputStreamWriter.println(gson.toJson(messObject));
					} else if (status.equals("PUBLISH")) {
						CommonMessModel mess = new CommonMessModel("PUBLISH", "ERROR");
						outputStreamWriter.println(gson.toJson(mess));
					} else if (status.equals("UNPUBLISH")) {
						CommonMessModel mess = new CommonMessModel("UNPUBLISH", "ERROR");
						outputStreamWriter.println(gson.toJson(mess));
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			if (!socket.isClosed())
				socket.close();
			if (socket.isClosed()) {
				service.closeHandler();
				System.out.println("The connection with the client is closed: " + service.getSessionId());
			}
		}
	}

}
