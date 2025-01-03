package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.AccountDecorator;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.services.DialogController;
import ro.uvt.dp.commands.AccountOperationsInvoker;
import ro.uvt.dp.database.DatabaseConnector;

import java.util.Random;

public class TransferDialogController implements DialogController {

    @FXML
    private ComboBox<Account> senderAccountComboBox;

    @FXML
    private TextField receiverAccountIdField;

    @FXML
    private TextField amountField;

    private Client client;

    private Stage dialogStage;

    @Override
    public void setClient(Client client) {
        this.client = client;
        populateSenderAccountComboBox();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void populateSenderAccountComboBox() {
        if (client != null && client.getAccounts() != null) {
            senderAccountComboBox.getItems().addAll(client.getAccounts());
        }
    }

    @FXML
    private void handleTransfer() {
        Account senderAccount = senderAccountComboBox.getValue();
        String receiverAccountId = receiverAccountIdField.getText();
        String amountText = amountField.getText();

        if (senderAccount == null) {
            showError("Please select a sender account.");
            return;
        }

        if (receiverAccountId.isEmpty()) {
            showError("Please enter a receiver account ID.");
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

        Account receiverAccount = findReceiverAccount(receiverAccountId);
        if (receiverAccount == null) {
            showError("Receiver account not found.");
            return;
        }

        if (!getBaseClass(senderAccount).equals(getBaseClass(receiverAccount))) {
            showError("Sender and receiver accounts must be of the same type.");
            return;
        }

        AccountOperationsInvoker invoker = new AccountOperationsInvoker();
        try {
            senderAccount.transferUsingCommand(invoker, receiverAccount, amount);
            showSuccess("Transfer successful. Generated PIN: " + generatePin());
        } catch (Exception e) {
            showError("Transfer failed: " + e.getMessage());
        }
    }

    private Account findReceiverAccount(String accountId) {
        return DatabaseConnector.findAccountById(accountId);
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

    private Class<?> getBaseClass(Account account) {
        while (account instanceof AccountDecorator) {
            account = ((AccountDecorator) account).getAccount();
        }
        return account.getClass();
    }

}
