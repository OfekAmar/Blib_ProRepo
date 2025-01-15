package server;

import java.sql.ResultSet;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

//the class uses to manage the connection between the client and the server uses OCSF library
//the class extaends AbstractServer (OCSF) in order to use handleMessageFromClient 
public class ServerMain extends AbstractServer {

	private final DBconnector dbConnector;

	public ServerMain(int port) {
		super(port); // call AbstractServer constructor with the port he needs to listen to
		this.dbConnector = new DBconnector(); // create the connection to DB
	}

	// the method used to handle the messages from the client
	// messages options:
	// READ - returns the list of all the subscribers from the database
	// UPDATE - search for subscriber by his ID and update the details the client
	// asked
	// SHOW - search for subscriber by his ID and return his details
	// all options use the DBconnector in order to fatch the data
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		try {
			if (msg instanceof String) {
				String[] parts = ((String) msg).split(",");
				String command = parts[0]; // parts[0] - the option the clients ask for

				if ("READ".equalsIgnoreCase(command)) {
					String response = dbConnector.readSubscribers();
					client.sendToClient(response);
				} else if ("UPDATE".equalsIgnoreCase(command) && parts.length == 5) {
					int id = Integer.parseInt(parts[1]);
					String name = parts[2];
					String email = parts[3];
					String phoneNumber = parts[4];
					dbConnector.updateSubscriber(id, name, email, phoneNumber);
					System.out.println("Updated Successfully");
				} else if ("SHOW".equalsIgnoreCase(command) && parts.length == 2) {
					String response = dbConnector.showSubscriber(Integer.parseInt(parts[1]));
					client.sendToClient(response);
				}
				else if("ADD_BOOK".equalsIgnoreCase(command) && parts.length == 6) {
					String bookCode = parts[1];
					String author = parts[2];
					String title = parts[3];
					String subject = parts[4];
					String description = parts[5];
					dbConnector.updateQuery("INSERT INTO book (book_code, author, title, subject, description) VALUES ('" 
		                    + bookCode + "', '" + author + "', '" + title + "', '" + subject + "', '" + description + "')");
				}
				else if ("DELETE_BOOK".equalsIgnoreCase(command) && parts.length == 2) {
	                String bookCode = parts[1];
	                try {
	                	 dbConnector.updateQuery("DELETE FROM book WHERE book_code = '" + bookCode + "'");
	                	 client.sendToClient("Book deleted successfully");
	                	 System.out.println("the book delte");
	                }catch(Exception e){
	                	System.out.println("the book dosent delte");
	                	client.sendToClient(e.getMessage());
	                }
	            
	            } else if ("EDIT_BOOK".equalsIgnoreCase(command) && parts.length == 6) {
	                String bookCode = parts[1];
	                String newAuthor = parts[2];
	                String newTitle = parts[3];
	                String newSubject = parts[4];
	                String newDescription = parts[5];
	                dbConnector.executeQuery("UPDATE book SET author = '" + newAuthor + "', title = '" + newTitle
	                    + "', subject = '" + newSubject + "', description = '" + newDescription + "' WHERE book_code = '" + bookCode + "'");
	                client.sendToClient("Book updated successfully");
	            } else if ("CHECK_BOOK_AVAILABILITY".equalsIgnoreCase(command) && parts.length == 2) {
	                String bookCode = parts[1];
	                ResultSet rs = dbConnector.executeQuery("SELECT * FROM book WHERE book_code = '" + bookCode + "'");
	                if (rs.next()) {
	                    client.sendToClient("Book is available");
	                } else {
	                    client.sendToClient("Book is not available");
	                }
	            } else if ("GET_BOOK_DETAILS".equalsIgnoreCase(command) && parts.length == 2) {
	                String bookCode = parts[1];
	                ResultSet rs = dbConnector.executeQuery("SELECT * FROM book WHERE book_code = '" + bookCode + "'");
	                if (rs.next()) {
	                    String details = rs.getString("book_code") + "," + rs.getString("author") + "," 
	                                    + rs.getString("title") + "," + rs.getString("subject") + "," 
	                                    + rs.getString("description");
	                    client.sendToClient(details);
	                }
	                
			}
			}

		} catch (Exception e) {
			try {
				client.sendToClient("ERROR: " + e.getMessage());
			} catch (Exception ignored) {
			}
		}
	}
	//using the AbstractServer tool to get the connected client IP and HostName
	@Override
	protected void clientConnected(ConnectionToClient client) {
		String clientInfo = "Client connected: IP = " + client.getInetAddress().getHostAddress() + ", Host = "
				+ client.getInetAddress().getHostName();
		System.out.println(clientInfo);
	}
	//using the AbstractServer tool to get the disconnected client IP and HostName
	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		System.out.println("Client disconnected: IP = " + client.getInetAddress().getHostAddress());
	}
}
