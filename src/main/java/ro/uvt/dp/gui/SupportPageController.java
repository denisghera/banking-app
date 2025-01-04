package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.support.CustomerSupport;
import ro.uvt.dp.support.AdminSupport;
import ro.uvt.dp.support.Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SupportPageController {

    @FXML
    private Label roleTitleLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private ListView<String> ticketListView;
    @FXML
    private Button refreshButton;

    private String username;
    private String supportLevel;

    public void initialize(String username, String supportLevel) {
        this.username = username;
        this.supportLevel = supportLevel;
        roleTitleLabel.setText("Logged in as " + supportLevel);

        ticketListView.setCellFactory(param -> new TicketListCell(this::resolveTicket));
        loadTickets();
    }

    private void loadTickets() {
        ticketListView.getItems().clear();
        List<Map<String, String>> tickets = DatabaseConnector.getTicketsForSupportLevel(supportLevel);
        for (Map<String, String> ticket : tickets) {
            String ticketDisplay = String.format(
                    "ID: %s\nPriority: %s\nMessage: %s\nResolved: %s\nTimestamp: %s\nAccount ID: %s",
                    ticket.get("id"),
                    ticket.get("priority"),
                    ticket.get("message"),
                    ticket.get("resolved"),
                    ticket.get("timestamp"),
                    ticket.get("account_id")
            );
            ticketListView.getItems().add(ticketDisplay);
        }
    }
    @FXML
    private void handleRefresh() {
        loadTickets();
    }
    private void resolveTicket(String ticketId) {
        List<Map<String, String>> tickets = DatabaseConnector.getTicketsForSupportLevel(supportLevel);
        Map<String, String> ticketDetails = null;

        for (Map<String, String> ticket : tickets) {
            if (ticket.get("id").equals(ticketId)) {
                ticketDetails = ticket;
                break;
            }
        }

        if (ticketDetails == null) {
            showAlert(AlertType.ERROR, "Error", "Ticket not found.");
            return;
        }

        Request.Priority priority = Request.Priority.valueOf(ticketDetails.get("priority").toUpperCase());
        String message = ticketDetails.get("message");
        String accountId = ticketDetails.get("account_id");

        Request request = new Request(priority, accountId, message);

        CustomerSupport customerSupport = new CustomerSupport();
        AdminSupport adminSupport = new AdminSupport();
        customerSupport.setNextHandler(adminSupport);

        boolean requestHandled = customerSupport.handleRequest(request);
        if (!requestHandled) {
            showAlert(AlertType.ERROR, "Error", "Failed to handle the request. Please check the logs for details.");
            return;
        }

        if (DatabaseConnector.resolveTicket(ticketId)) {
            showAlert(AlertType.INFORMATION, "Success", "Ticket resolved successfully!");
            loadTickets();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to resolve ticket.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
