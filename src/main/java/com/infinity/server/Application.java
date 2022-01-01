package com.infinity.server;

import com.infinity.server.controllers.ServerListener;

public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new ServerListener();
			ServerListener.Listener();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR");
			e.printStackTrace();
		}

	}

}
