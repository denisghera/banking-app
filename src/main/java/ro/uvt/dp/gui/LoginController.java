package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.LimitExceededException;
import ro.uvt.dp.server.NetworkClient;
import ro.uvt.dp.entities.ClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Map<String, String> userDetails = DatabaseConnector.getUserDetails(username);
        Map<String, String> roleDetails = DatabaseConnector.getUserRole(username);

        if (userDetails == null) {
            if (roleDetails != null) {
                String supportLevel = roleDetails.get("support_level");
                if ("customer support".equalsIgnoreCase(supportLevel) || "admin".equalsIgnoreCase(supportLevel)) {
                    try (NetworkClient networkClient = new NetworkClient("CS")) {
                        String response = networkClient.login(username, password);
                        if (response.startsWith("SUCCESS")) {
                            switchToSupportPage(username, supportLevel);
                        } else {
                            showError(response);
                        }
                    } catch (IOException e) {
                        showError("Error: " + e.getMessage());
                    }
                    return;
                }
            }
            showError("Invalid credentials!");
            return;
        }

        String bankID = userDetails.get("bankID");
        try (NetworkClient networkClient = new NetworkClient(bankID)) {
            String response = networkClient.login(username, password);

            if (response.startsWith("SUCCESS")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.setContentText("Login successful for " + username);
                alert.showAndWait();

                Client client = buildClient(userDetails);
                switchToMainPage(client);
            } else {
                showError(response);
            }
        } catch (IOException | LimitExceededException e) {
            showError("Error: " + e.getMessage());
        }
    }
    private void switchToSupportPage(String username, String supportLevel) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("supportPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        SupportPageController controller = fxmlLoader.getController();
        controller.initialize(username, supportLevel);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Customer Support");
        stage.setScene(scene);
        stage.show();
    }
    private Client buildClient(Map<String, String> userDetails) throws LimitExceededException {
        List<Account> accounts = DatabaseConnector.getClientAccounts(userDetails.get("username"));

        ClientBuilder clientBuilder = (ClientBuilder) new ClientBuilder()
                .setUsername(userDetails.get("username"))
                .setName(userDetails.get("name"))
                .setAddress(userDetails.get("address"))
                .setEmail(userDetails.get("email"))
                .setBankID(userDetails.get("bankID"));

        for (Account account : accounts) {
            clientBuilder.addAccount(account);
        }

        return clientBuilder.build();
    }
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void goToSignup() throws IOException {
        switchToSignupPage();
    }
    private void switchToMainPage(Client client) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("accountDetails.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 420);

        AccountDetailsController controller = fxmlLoader.getController();
        controller.setClient(client);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Main Application");
        stage.setScene(scene);
        stage.show();
    }
    private void switchToSignupPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 420);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Signup");
        stage.setScene(scene);
        stage.show();
    }
}
