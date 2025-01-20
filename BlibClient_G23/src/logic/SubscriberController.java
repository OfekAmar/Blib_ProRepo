package logic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;

public class SubscriberController {
    private final ClientMain client;
    private Object response;
    private CountDownLatch latch;

    public SubscriberController(ClientMain client) {
        this.client = client;
    }

    private synchronized void sendSynchronizedMessage(String message) {
        client.sendMessageToServer(message);
    }

    public void addSubscriber(String subscriberId, String name, String phone, String email, String status) throws InterruptedException {
        
    }

    public void deleteSubscriber(String subscriberId) throws InterruptedException {
        
    }

    public void editSubscriber(String subscriberId, String name, String phone, String email, String status) throws InterruptedException {
        
    }

    public synchronized String checkSubscriberStatus(String subscriberId) throws InterruptedException {
        String msg = "CHECK_SUBSCRIBER_STATUS," + subscriberId;
        latch = new CountDownLatch(1);
        client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
        });
        
        client.sendMessageToServer(msg);
        latch.await();
		return (String) (response);

    }

    public void freezeSubscriber(String subscriberId) throws InterruptedException {
        String msg = "FREEZE_SUBSCRIBER," + subscriberId;
        sendSynchronizedMessage(msg);
        TimeUnit.MILLISECONDS.sleep(500);
    }

    public void unfreezeSubscriber(String subscriberId) throws InterruptedException {
       
    }

    public void getSubscriberHistory(String subscriberId) throws InterruptedException {
      
    }

    public void notifySubscriber(String subscriberId, String message) throws InterruptedException {
        String msg = "NOTIFY_SUBSCRIBER," + subscriberId + "," + message;
        sendSynchronizedMessage(msg);
        TimeUnit.MILLISECONDS.sleep(500);
    }

    public void viewSubscriberCard(String subscriberId) throws InterruptedException {
        String msg = "VIEW_SUBSCRIBER_CARD," + subscriberId;
        sendSynchronizedMessage(msg);
        TimeUnit.MILLISECONDS.sleep(500);
    }
}
