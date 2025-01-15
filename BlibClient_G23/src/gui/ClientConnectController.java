package gui;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientConnectController {

    private ClientMain client;

    @FXML
    private TextField portField;

    @FXML
    private TextField hostField;

    @FXML
    public void connectToServer() {
        String port = portField.getText();
        String host = hostField.getText();

        if (port == null || port.isEmpty() || host == null || host.isEmpty()) {
            System.out.println("IP or Port is empty. Please enter valid details.");
            return;
        }

        try {
            client = new ClientMain(host, Integer.parseInt(port));
            client.openConnection();
            System.out.println("Connected to server at " + host + ":" + port);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientUI.fxml"));
            Parent root = loader.load();

            ClientUIController controller = loader.getController();
            controller.setClient(client);
            controller.setStage((Stage) portField.getScene().getWindow());

            Stage stage = (Stage) portField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Client UI");

        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
