package logic;

import java.util.concurrent.CountDownLatch;

import client.ClientMain;
import gui.SubscriberMainController;

public class LoginLogic {
	private ClientMain client;
	private Object response;
	private CountDownLatch latch;
	
	public LoginLogic(ClientMain client) {
		this.client=client;
	}
	
	public synchronized Object login(String userName, String password) throws InterruptedException {
		String msg="LOGIN,"+userName+","+password;
		latch=new CountDownLatch(1);
		
		client.setMessageHandler((Object serverResponse) -> {
			if(serverResponse instanceof Subscriber) {
				try {
					Subscriber sub=(Subscriber)serverResponse;
					this.response=sub;
				}catch(ClassCastException e) {
					System.err.println("Failed to cast response to Subscriber: "+e.getMessage());
				}
			}else if(serverResponse instanceof Librarian) {
				try {
					Librarian lib=(Librarian)serverResponse;
					this.response=lib;
				}catch(ClassCastException e) {
					System.err.println("Failed to cast response to Librarian: "+e.getMessage());
				}
			}else if(serverResponse instanceof String) {
				System.out.println(serverResponse);
			}
			else {
				System.err.println("Unexpected response type from server: "+serverResponse.getClass().getName());
			}
			latch.countDown();
		});
		
		client.sendMessageToServer(msg);
		latch.await();
		return response;
	}
}
