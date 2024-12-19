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
import ro.uvt.dp.server.NetworkClient;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String bankID = DatabaseConnector.getUserBankID(username);
        if (bankID == null) {
            showError("Invalid credentials!");
            return;
        }

        try (NetworkClient networkClient = new NetworkClient(bankID)) {
            String response = networkClient.login(username, password);

            if (response.startsWith("SUCCESS")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.setContentText("Login successful for " + username);
                alert.showAndWait();

                switchToMainPage();
            } else {
                showError(response);
            }
        } catch (IOException e) {
            showError("Error communicating with the server: " + e.getMessage());
        }
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

    private void switchToMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("accountDetails.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
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
