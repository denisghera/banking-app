package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.server.NetworkClient;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.List;

public class SignupController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField fullnameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> bankComboBox;

    @FXML
    public void initialize() {
        List<String> bankIDs = DatabaseConnector.getBankIDs();
        bankComboBox.getItems().addAll(bankIDs);
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText();
        String fullname = fullnameField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String bankID = bankComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || fullname.isEmpty() || address.isEmpty() || bankID == null) {
            showError("Error", "All fields must be filled.");
            return;
        }
        if (!isValidEmail(email)) {
            showError("Error", "Invalid email format.");
            return;
        }
        if (password.length() < 8) {
            showError("Error", "Password must be at least 8 characters long.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Error", "Passwords do not match.");
            return;
        }

        try (NetworkClient networkClient = new NetworkClient(bankID)) {
            String response = networkClient.signup(username, fullname, email, address, password);

            if (response.startsWith("SUCCESS")) {
                showAlert("Success", "User " + username + " signed up successfully!");
                goToLoginPage();
            } else {
                showError("Error", response);
            }
        } catch (IOException e) {
            showError("Failed to communicate with the bank server",e.getMessage());
        }
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void goToLoginPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
