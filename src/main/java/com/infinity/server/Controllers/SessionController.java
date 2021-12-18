package com.infinity.server.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.server.Application;
import com.infinity.server.Models.CommonMessModel;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import static com.mongodb.client.model.Filters.eq;

public class SessionController extends Thread {

	private Socket socket;

	private CommonMessModel commonMess;

	private BufferedReader inputStreamReader;

	private PrintWriter outputStreamWriter;

	private JSONObject ackMessJsonObject;

	private static final Logger LOGGER = LogManager.getLogger(SessionController.class);

	public SessionController() {
		// TODO Auto-generated constructor stub
	}

	public SessionController(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}

	public void run() {
		try {
			inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
			String ackMessage = inputStreamReader.readLine();
			ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
			String status = ackMessJsonObject.getString("status");
			commonMess = new CommonMessModel();

			if (status.equals("CONNECT")) {
				commonMess.setStatus("CONNECT");
				LOGGER.info(ackMessJsonObject.getString("username"));
				connectClient();
				commonMess.setMessage("ACCEPT");
				outputStreamWriter.println(JSON.toJSONString(commonMess));
			} else if (status.equals("SEARCH")) {
				commonMess.setStatus("SEARCH");
				searchFile();
			} else if (status.equals("PUBLISH")) {

			} else if (status.equals("UNPUBLISH")) {

			} else if (status.equals("INFOREQUEST")) {

			} else if (status.equals("QUIT")) {
				closeSocket();
			}

		} catch (Exception e) {
			LOGGER.warn(e);
		} finally {
			closeSocket();
		}
	}

	public void connectClient() {
		JSONObject messObject = ackMessJsonObject.getJSONObject("payload");
		DBObject dbObject = (DBObject) messObject;
		try {
			Application.collection.insert(dbObject);
		} catch (Exception e) {
			// TODO: handle exception
			commonMess.setMessage("REFUSE");
		}
	}

	public void searchFile() {
		String messString = ackMessJsonObject.getString("payload");
		DBObject data = Application.collection.findOne(eq("fileName", messString));
		LOGGER.info(data);
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
