package com.infinity.server.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import javax.swing.text.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.server.Application;
import com.infinity.server.models.CommonMessModel;
import com.infinity.server.models.InfoRequestMessModel;
import com.infinity.server.models.SearchMessModel;
import com.infinity.server.models.SharedFileModel;
import com.infinity.server.models.SharedFileModelv2;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import static com.mongodb.client.model.Filters.eq;

public class SessionController extends Thread {

	private Socket socket;

	private CommonMessModel commonMess;

	private BufferedReader inputStreamReader;

	private PrintWriter outputStreamWriter;

	private JSONObject ackMessJsonObject;

	private String clientIpAddress;

	private int commandPort;

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

			while (true) {
				String ackMessage = inputStreamReader.readLine();
				System.out.println("client: " + ackMessage);

				ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
				String status = ackMessJsonObject.getString("status");
				commonMess = new CommonMessModel();

				if (status.equals("CONNECT")) {
					commonMess.setStatus("CONNECT");

					connectClient();

					System.out.println(JSON.toJSONString(commonMess));

					outputStreamWriter.println(JSON.toJSONString(commonMess));
				} else if (status.equals("SEARCH")) {

					searchFile();

				} else if (status.equals("PUBLISH")) {
					commonMess.setStatus("PUBLISH");
					publicFileHandler();

					System.out.println(JSON.toJSONString(commonMess));

					outputStreamWriter.println(JSON.toJSONString(commonMess));
				} else if (status.equals("UNPUBLISH")) {
					commonMess.setStatus("UNPUBLISH");
					unPublicFileHandler();

					commonMess.setMessage("SUCCESS");
					System.out.println(JSON.toJSONString(commonMess));

					outputStreamWriter.println(JSON.toJSONString(commonMess));
				} else if (status.equals("INFOREQUEST")) {
					infoRequestHandler();

				} else if (status.equals("ERRDOWNLOAD")) {
					errorDownloadHandler();
				} else if (status.equals("QUIT")) {

					closeSocket();
				}

			}
		} catch (Exception e) {
			LOGGER.warn(e);
		}
	}

	public void connectClient() {
		try {
			List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
			sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);

			InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			clientIpAddress = socketAddress.getAddress().getHostAddress();
			commandPort = ackMessJsonObject.getIntValue("commandPort");

			if (sharedFiles.size() != 0) {
				ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();

				while (iterator.hasNext()) {
					SharedFileModel element = iterator.next();
					BasicDBObject doc1 = new BasicDBObject();

					doc1.append("fileName", element.getFileName() + "");
					doc1.append("filePath", element.getFilePath() + "");
					doc1.append("sharer", element.getSharer() + "");
					doc1.append("checksum", element.getChecksum() + "");
					doc1.append("size", element.getSize() + "");
					doc1.append("clientIPAddress", clientIpAddress + "");
					doc1.append("commandPort", commandPort + "");
					doc1.append("errorCount", 0);

					Application.collection.insert(doc1);
				}
			}

			commonMess.setMessage("ACCEPT");
		} catch (Exception e) {
			System.out.println(e);
			commonMess.setMessage("REFUSE");
		}

	}

	public void publicFileHandler() {
		List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
		try {
			sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);

			if (sharedFiles.size() != 0) {
				ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();
				while (iterator.hasNext()) {
					SharedFileModel element = iterator.next();
					BasicDBObject doc1 = new BasicDBObject();

					doc1.append("fileName", element.getFileName() + "");
					doc1.append("filePath", element.getFilePath() + "");
					doc1.append("sharer", element.getSharer() + "");
					doc1.append("checksum", element.getChecksum() + "");
					doc1.append("size", element.getSize() + "");
					doc1.append("clientIPAddress", clientIpAddress + "");
					doc1.append("commandPort", commandPort + "");

					Application.collection.insert(doc1);
				}
			}
			commonMess.setMessage("SUCCESS");
		} catch (Exception e) {
			commonMess.setMessage("ERROR");
		}
	}

	public void unPublicFileHandler() {
//		List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
//		sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);
//
//		if (sharedFiles.size() != 0) {
//			ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();
//			while (iterator.hasNext()) {
//				SharedFileModel element = iterator.next();
//				BasicDBObject query = new BasicDBObject();
//
//				query.append("fileName", element.getFileName() + "");
//				query.append("filePath", element.getFilePath() + "");
//				query.append("sharer", element.getSharer() + "");
//				query.append("checksum", element.getChecksum() + "");
//				query.append("size", element.getSize() + "");
//
//				Application.collection.remove(query);
//			}
//		}
		List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
		try {
			sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);

			if (sharedFiles.size() != 0) {
				ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();
				while (iterator.hasNext()) {
					SharedFileModel element = iterator.next();
					BasicDBObject doc1 = new BasicDBObject();

					doc1.append("fileName", element.getFileName() + "");
					doc1.append("filePath", element.getFilePath() + "");
					doc1.append("sharer", element.getSharer() + "");
					doc1.append("checksum", element.getChecksum() + "");
					doc1.append("size", element.getSize() + "");
					doc1.append("clientIPAddress", clientIpAddress + "");
					doc1.append("commandPort", commandPort + "");

					Application.collection.remove(doc1);
				}
				commonMess.setMessage("SUCCESS");
			}
		} catch (Exception e) {
			commonMess.setMessage("ERROR");
		}
	}

	public void searchFile() {
		SearchMessModel searchMess = new SearchMessModel();
		searchMess.setStatus("SEARCH");

		String messString = ackMessJsonObject.getString("payload");

		Pattern pattern = Pattern.compile(messString, Pattern.CASE_INSENSITIVE);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("fileName", pattern);

		DBCursor cursor = Application.collection.find(whereQuery);

		tryLable: try {
			if (!cursor.hasNext()) {
				searchMess.setMessage("ERROR");
				break tryLable;
			}
			while (cursor.hasNext()) {
				DBObject element = cursor.next();
				element.removeField("_id");
				SharedFileModelv2 data = JSON.parseObject(element.toString(), SharedFileModelv2.class);

				if ((data.getClientIpAddress().equals(clientIpAddress)) && (data.getCommandPort() == commandPort)) {
					continue;
				}
				searchMess.addSharedFile(data);
			}
			if (searchMess.getPayload().size() > 0) {
				searchMess.setMessage("SUCCESS");
			} else {
				searchMess.setMessage("ERROR");
			}
		} catch (Exception e) {
			searchMess.setMessage("ERROR");
		}

		System.out.println(JSON.toJSONString(searchMess));
		outputStreamWriter.println(JSON.toJSONString(searchMess));
	}

	public void infoRequestHandler() {
		InfoRequestMessModel infoMess = new InfoRequestMessModel();
		infoMess.setStatus("INFOREQUEST");

		JSONObject payloadJsonObject = new JSONObject();
		try {
			SharedFileModel data = JSON.parseObject(ackMessJsonObject.getString("payload"), SharedFileModel.class);

			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("fileName", data.getFileName() + "");
			whereQuery.put("filePath", data.getFilePath() + "");
			whereQuery.put("sharer", data.getSharer() + "");
			whereQuery.put("checksum", data.getChecksum() + "");
			whereQuery.put("size", data.getSize() + "");

			DBCursor cursor = Application.collection.find(whereQuery);

			if (!cursor.hasNext()) {
				payloadJsonObject.put("ip", "N/a");
				payloadJsonObject.put("commandPort", -1);
			} else {
				DBObject element = cursor.next();

				payloadJsonObject.put("ip", element.get("clientIPAddress"));
				payloadJsonObject.put("commandPort", element.get("commandPort"));
			}

		} catch (Exception e) {
			payloadJsonObject.put("ip", "N/a");
			payloadJsonObject.put("commandPort", -1);
		}

		infoMess.setPayload(payloadJsonObject);

		System.out.println(JSON.toJSONString(infoMess));
		outputStreamWriter.println(JSON.toJSONString(infoMess));

	}

	private void closeSocket() {
		if (socket == null) {
			return;
		}

		try {
			// remove
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.append("clientIPAddress", clientIpAddress +"");
			whereQuery.append("commandPort", commandPort + "");

			Application.collection.remove(whereQuery);

			socket.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void errorDownloadHandler() {
		try {
			SharedFileModel data = JSON.parseObject(ackMessJsonObject.getString("payload"), SharedFileModel.class);

			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("fileName", data.getFileName() + "");
			whereQuery.put("filePath", data.getFilePath() + "");
			whereQuery.put("sharer", data.getSharer() + "");
			whereQuery.put("checksum", data.getChecksum() + "");
			whereQuery.put("size", data.getSize() + "");

			DBCursor cursor = Application.collection.find(whereQuery);

			if (cursor.hasNext()) {
				Application.collection.remove(whereQuery);
				BSONObject elementBsonObject = cursor.next();
				
				int count = Integer.parseInt(elementBsonObject.get("errorCount").toString());
				if(count > 2) {
					return;
				}
				whereQuery.put("clientIPAddress", elementBsonObject.get("clientIPAddress") +"");
				whereQuery.put("commandPort", elementBsonObject.get("commandPort"));
				whereQuery.put("errorCount", count + 1);
				Application.collection.insert(whereQuery);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
