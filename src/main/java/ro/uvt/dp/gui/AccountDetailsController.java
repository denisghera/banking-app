package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ro.uvt.dp.accounts.AccountEUR;
import ro.uvt.dp.accounts.AccountRON;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.AccountDecorator;
import ro.uvt.dp.entities.Client;

import java.io.IOException;
import java.util.List;

public class AccountDetailsController {
    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientEmailLabel;
    @FXML
    private Label accountNumberLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private ComboBox<Account> accountDropdown;
    @FXML
    private Button requestAccountButton;
    @FXML
    private Button goToOperationsButton;
    @FXML
    private Button additionalAccountButton;

    private Client client;
    public void setClient(Client client) {
        this.client = client;

        clientNameLabel.setText("Name: " + client.getName());
        clientEmailLabel.setText("Email: " + client.getEmail());

        initializeAccounts();
    }
    private void initializeAccounts() {
        List<Account> accounts = client.getAccounts();

        if (accounts.isEmpty()) {
            requestAccountButton.setVisible(true);
            additionalAccountButton.setVisible(false);
            accountNumberLabel.setVisible(false);
            balanceLabel.setVisible(false);
            accountDropdown.setVisible(false);
            goToOperationsButton.setDisable(true);
        } else {
            if (accounts.size() == 1) {
                requestAccountButton.setVisible(false);
                accountDropdown.setVisible(false);
                displayAccountDetails(accounts.get(0));
            } else {
                requestAccountButton.setVisible(false);
                accountDropdown.getItems().addAll(accounts);
                accountDropdown.setVisible(true);

                displayAccountDetails(accounts.get(0));
            }
            additionalAccountButton.setVisible(true);
            goToOperationsButton.setDisable(false);
            accountNumberLabel.setVisible(true);
            balanceLabel.setVisible(true);
        }
    }
    @FXML
    private void onAccountSelection() {
        Account selectedAccount = accountDropdown.getValue();
        if (selectedAccount != null) {
            displayAccountDetails(selectedAccount);
        }
    }
    private void displayAccountDetails(Account account) {
        accountNumberLabel.setText("Account Number: " + account.getAccountCode());
        String currency = "";
        Account baseAccount = account instanceof AccountDecorator ? ((AccountDecorator) account).getBaseAccount() : account;

        if (baseAccount instanceof AccountEUR) {
            currency = "€";
        } else if (baseAccount instanceof AccountRON) {
            currency = "RON";
        }

        balanceLabel.setText("Balance: " + currency + account.getAmount());
        accountNumberLabel.setVisible(true);
        balanceLabel.setVisible(true);
    }
    @FXML
    private void requestNewAccount() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("accountCreation.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 320);

            AccountCreationController controller = fxmlLoader.getController();
            controller.setClient(client);

            Stage stage = (Stage) requestAccountButton.getScene().getWindow();
            stage.setTitle("Create New Account");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToOperations() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("operationsCenter.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 380);

            OperationsCenterController controller = fxmlLoader.getController();
            controller.setClient(client);

            Stage stage = (Stage) clientNameLabel.getScene().getWindow();
            stage.setTitle("Operations Center");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void logout() {
        client = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = (Stage) clientNameLabel.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
