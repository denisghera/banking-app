package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.uvt.dp.commands.AccountOperationsInvoker;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.services.DialogController;

import java.util.Random;

public class RetrieveDialogController implements DialogController {

    @FXML
    private ComboBox<Account> accountComboBox;

    @FXML
    private TextField amountField;

    private Client client;

    private Stage dialogStage;

    @Override
    public void setClient(Client client) {
        this.client = client;
        populateAccountComboBox();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void populateAccountComboBox() {
        if (client != null && client.getAccounts() != null) {
            accountComboBox.getItems().addAll(client.getAccounts());
        }
    }

    @FXML
    private void handleRetrieve() {
        Account selectedAccount = accountComboBox.getValue();
        String amountText = amountField.getText();

        if (selectedAccount == null) {
            showError("Please select an account.");
            return;
        }

        if (amountText.isEmpty()) {
            showError("Please enter an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                throw new NumberFormatException("Amount must be greater than zero.");
            }
        } catch (NumberFormatException e) {
            showError("Invalid amount entered. Please enter a positive number.");
            return;
        }

        if (amount > selectedAccount.getAmount()) {
            showError("Insufficient funds in the selected account.");
            return;
        }

        int pin = generatePin();

        AccountOperationsInvoker invoker = new AccountOperationsInvoker();
        try {
            selectedAccount.retrieveUsingCommand(invoker, amount);

            showSuccess("Retrieve successful. Generated PIN: " + pin);
        } catch (RuntimeException e) {
            showError(e.getMessage() + "\nTry again later!");
        } catch (Exception e) {
            showError("Retrieve failed: " + e.getMessage());
        }
    }

    private int generatePin() {
        return new Random().nextInt(9000) + 1000;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
