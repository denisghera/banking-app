package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.uvt.dp.accounts.AccountEURFactory;
import ro.uvt.dp.accounts.AccountRONFactory;
import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountFactory;

import java.io.IOException;

public class AccountCreationController {
    @FXML
    private ComboBox<String> currencyDropdown;
    @FXML
    private TextField initialAmountField;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
        initializeCurrencyDropdown();
    }
    private void initializeCurrencyDropdown() {
        currencyDropdown.getItems().addAll("EUR", "RON");
    }
    @FXML
    private void handleAccountCreation() {
        String selectedCurrency = currencyDropdown.getValue();
        String initialAmountText = initialAmountField.getText();

        if (selectedCurrency == null || initialAmountText.isEmpty()) {
            showError("Please select a currency and enter an initial amount.");
            return;
        }

        try {
            double initialAmount = Double.parseDouble(initialAmountText);
            AccountFactory factory = getAccountFactory(selectedCurrency);
            Account newAccount = factory.create(initialAmount);

            client.addAccount(newAccount);

            DatabaseConnector.addAccount(client.getUsername(), newAccount, selectedCurrency);

            showSuccess("Account created successfully!");
            goBack();

        } catch (NumberFormatException e) {
            showError("Invalid initial amount. Please enter a valid number.");
        } catch (InvalidAmountException e) {
            showError("Error creating account: " + e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    private AccountFactory getAccountFactory(String currency) {
        switch (currency) {
            case "EUR":
                return new AccountEURFactory();
            case "RON":
                return new AccountRONFactory();
            default:
                throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }
    @FXML
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("accountDetails.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 420);

            AccountDetailsController controller = fxmlLoader.getController();
            controller.setClient(client);

            Stage stage = (Stage) currencyDropdown.getScene().getWindow();
            stage.setTitle("Account Details");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Account Creation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
