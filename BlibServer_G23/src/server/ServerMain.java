package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ServerMain extends AbstractServer {

    private final DBconnector dbConnector;

    public ServerMain(int port) {
        super(port);
        this.dbConnector = new DBconnector();
    }

    @Override
    protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Processing message from client: " + msg);

        try {
            if (msg instanceof String) {
                String[] parts = ((String) msg).split(",");
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "ADD_BOOK":
                        if (parts.length == 6) {
                            String bookCode = parts[1];
                            String author = parts[2];
                            String title = parts[3];
                            String subject = parts[4];
                            String description = parts[5];
                            dbConnector.updateQuery("INSERT INTO book (book_code, author, title, subject, description) VALUES ('"
                                    + bookCode + "', '" + author + "', '" + title + "', '" + subject + "', '" + description + "')");
                            client.sendToClient("Book added successfully: " + title);
                        } else {
                            client.sendToClient("ERROR: Invalid ADD_BOOK format.");
                        }
                        break;

                    case "DELETE_BOOK":
                        if (parts.length == 2) {
                            String bookCode = parts[1];
                            int rowsAffected = dbConnector.updateQuery("DELETE FROM book WHERE book_code = '" + bookCode + "'");
                            if (rowsAffected > 0) {
                                client.sendToClient("Book deleted successfully: " + bookCode);
                            } else {
                                client.sendToClient("Book not found: " + bookCode);
                            }
                        } else {
                            client.sendToClient("ERROR: Invalid DELETE_BOOK format.");
                        }
                        break;

                    case "CHECK_BOOK_AVAILABILITY":
                        if (parts.length == 2) {
                            String bookCode = parts[1];
                            ResultSet rs = dbConnector.executeQuery("SELECT * FROM book WHERE book_code = '" + bookCode + "'");
                            if (rs.next()) {
                                client.sendToClient("Book is available: " + bookCode);
                            } else {
                                client.sendToClient("Book is not available: " + bookCode);
                            }
                        } else {
                            client.sendToClient("ERROR: Invalid CHECK_BOOK_AVAILABILITY format.");
                        }
                        break;

                    case "CHECK_SUBSCRIBER_STATUS":
                        if (parts.length == 2) {
                            int subscriberId = Integer.parseInt(parts[1]);
                            String query = "SELECT status FROM Subscriber WHERE sub_id = ?";
                            try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
                                ps.setInt(1, subscriberId);
                                ResultSet rs = ps.executeQuery();
                                if (rs.next()) {
                                    String status = rs.getString("status");
                                    client.sendToClient("Subscriber status: " + status);
                                } else {
                                    client.sendToClient("ERROR: Subscriber not found.");
                                }
                            }
                        } else {
                            client.sendToClient("ERROR: Invalid CHECK_SUBSCRIBER_STATUS format.");
                        }
                        break;

                    case "FREEZE_SUBSCRIBER":
                        if (parts.length == 2) {
                            int subscriberId = Integer.parseInt(parts[1]);
                            String query = "UPDATE Subscriber SET status = 'frozen' WHERE sub_id = ?";
                            try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
                                ps.setInt(1, subscriberId);
                                int rowsAffected = ps.executeUpdate();
                                if (rowsAffected > 0) {
                                    client.sendToClient("Subscriber frozen successfully.");
                                } else {
                                    client.sendToClient("ERROR: Subscriber not found.");
                                }
                            }
                        } else {
                            client.sendToClient("ERROR: Invalid FREEZE_SUBSCRIBER format.");
                        }
                        break;

                    case "NOTIFY_SUBSCRIBER":
                        if (parts.length == 3) {
                            int subscriberId = Integer.parseInt(parts[1]);
                            String message = parts[2];
                            System.out.println("Notification to Subscriber " + subscriberId + ": " + message);
                            client.sendToClient("Notification sent to Subscriber " + subscriberId + ".");
                        } else {
                            client.sendToClient("ERROR: Invalid NOTIFY_SUBSCRIBER format.");
                        }
                        break;

                    case "VIEW_SUBSCRIBER_CARD":
                        if (parts.length == 2) {
                            int subscriberId = Integer.parseInt(parts[1]);
                            String query = "SELECT * FROM Subscriber WHERE sub_id = ?";
                            try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
                                ps.setInt(1, subscriberId);
                                ResultSet rs = ps.executeQuery();
                                if (rs.next()) {
                                    String subscriberCard = "ID: " + rs.getInt("sub_id") + ", Name: " + rs.getString("sub_name") +
                                            ", Phone: " + rs.getString("phone_num") + ", Email: " + rs.getString("email_address") +
                                            ", Status: " + rs.getString("status");
                                    client.sendToClient("Subscriber Card: " + subscriberCard);
                                } else {
                                    client.sendToClient("ERROR: Subscriber not found.");
                                }
                            }
                        } else {
                            client.sendToClient("ERROR: Invalid VIEW_SUBSCRIBER_CARD format.");
                        }
                        break;

                    default:
                        client.sendToClient("ERROR: Unknown command.");
                        break;
                }
            } else {
                client.sendToClient("ERROR: Invalid message format.");
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            try {
                client.sendToClient("ERROR: " + e.getMessage());
            } catch (Exception ignored) {
            }
        }

        System.out.println("Finished processing message from client: " + msg);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected: IP = " + client.getInetAddress().getHostAddress() + ", Host = " + client.getInetAddress().getHostName());
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        System.out.println("Client disconnected: IP = " + client.getInetAddress().getHostAddress());
    }
}
