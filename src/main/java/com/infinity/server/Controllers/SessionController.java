package com.infinity.server.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.infinity.server.Application;
import com.infinity.server.Models.CommonMessModel;
import com.infinity.server.Models.SharedFileModel;
import com.mongodb.BasicDBObject;
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
			System.out.println(ackMessage);

			ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
			String status = ackMessJsonObject.getString("status");
			commonMess = new CommonMessModel();

			if (status.equals("CONNECT")) {
				commonMess.setStatus("CONNECT");
				LOGGER.info(ackMessJsonObject.getString("username"));
				connectClient();
				commonMess.setMessage("ACCEPT");
				System.out.println(JSON.toJSONString(commonMess));

				outputStreamWriter.println(JSON.toJSONString(commonMess));
				
				System.out.println("Connected");
			} else if (status.equals("SEARCH")) {
				System.out.println("search func");
				commonMess.setStatus("SEARCH");
				searchFile();
				commonMess.setMessage("ERROR");
				System.out.println(JSON.toJSONString(commonMess));

				outputStreamWriter.println(JSON.toJSONString(commonMess));
			} else if (status.equals("PUBLISH")) {
				commonMess.setStatus("PUBLISH");
				publicFileHandler();
				
				commonMess.setMessage("SUCCESS");
				System.out.println(JSON.toJSONString(commonMess));

				outputStreamWriter.println(JSON.toJSONString(commonMess));
				
				System.out.println("published");
			} else if (status.equals("UNPUBLISH")) {
				commonMess.setStatus("UNPUBLISH");
				unPublicFileHandler();
				
				commonMess.setMessage("SUCCESS");
				System.out.println(JSON.toJSONString(commonMess));

				outputStreamWriter.println(JSON.toJSONString(commonMess));
				
				System.out.println("unpublished");
			} else if (status.equals("INFOREQUEST")) {

			} else if (status.equals("QUIT")) {
				closeSocket();
			}

		} catch (Exception e) {
			LOGGER.warn(e);
		} 
//		finally {
//			closeSocket();
//		}
	}

	public void connectClient() {
//		JSONArray messArray = ackMessJsonObject.getJSONArray("payload");
//		System.out.println(messArray);
		List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
		sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);
		
		if(sharedFiles.size() !=0) {
			ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();
			
			while(iterator.hasNext()) {
				 
				 SharedFileModel element = iterator.next();
				 BasicDBObject doc1 = new BasicDBObject();
				 
			     doc1.append("fileName", element.getFileName()+ "");
			     doc1.append("filePath",  element.getFilePath()+ "");
			     doc1.append("sharer",  element.getSharer()+ "");
			     doc1.append("checksum",  element.getChecksum()+ "");
			     doc1.append("size",  element.getSize()+ "");

			     Application.collection.insert(doc1);
			}
		}
//		if (messArray.isEmpty() != true) {
//			System.out.println("not emty");
//			
//			JSONObject object = messArray.getJSONObject(0);
//			DBObject dbObject = (DBObject) JSON.parse(object.toString());
//			System.out.println(dbObject);
//			Application.collection.insert(dbObject);

	}
	
	public void  publicFileHandler() {
		System.out.println(ackMessJsonObject);
		List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
		sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);
		
		if(sharedFiles.size() !=0) {
			ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();
			while(iterator.hasNext()) {
				 SharedFileModel element = iterator.next();
				 BasicDBObject doc1 = new BasicDBObject();
				 
			     doc1.append("fileName", element.getFileName()+ "");
			     doc1.append("filePath",  element.getFilePath()+ "");
			     doc1.append("sharer",  element.getSharer()+ "");
			     doc1.append("checksum",  element.getChecksum()+ "");
			     doc1.append("size",  element.getSize()+ "");

			     Application.collection.insert(doc1);
			}
		}
	}
	
	public void  unPublicFileHandler() {
		System.out.println(ackMessJsonObject);
		List<SharedFileModel> sharedFiles = new ArrayList<SharedFileModel>();
		sharedFiles = JSON.parseArray(ackMessJsonObject.getString("payload"), SharedFileModel.class);
		
		if(sharedFiles.size() !=0) {
			ListIterator<SharedFileModel> iterator = sharedFiles.listIterator();
			while(iterator.hasNext()) {
				 SharedFileModel element = iterator.next();
				 BasicDBObject query = new BasicDBObject();
				 
				 query.append("fileName", element.getFileName()+ "");
				 query.append("filePath",  element.getFilePath()+ "");
				 query.append("sharer",  element.getSharer()+ "");
				 query.append("checksum",  element.getChecksum()+ "");
				 query.append("size",  element.getSize()+ "");

			     Application.collection.remove(query);
			}
		}
	}

	public void searchFile() {
		String messString = ackMessJsonObject.getString("payload");
		DBObject data = Application.collection.findOne(eq("fileName", messString));
		System.out.println(data);
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
