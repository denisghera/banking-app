package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.services.DialogController;

import java.util.Random;

public class RetrieveDialogController implements DialogController {

    @FXML
    private TextField amountField;

    private Client client;

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleRetrieve() {
        String amount = amountField.getText();

        if (amount.isEmpty()) {
            System.out.println("Please enter an amount.");
            return;
        }

        // Generate a random PIN
        int pin = new Random().nextInt(9000) + 1000;
        System.out.println("Retrieve amount: " + amount + " for client " + client.getUsername() + ". PIN: " + pin);
    }
}
