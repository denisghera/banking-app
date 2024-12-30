package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.services.DialogController;

public class TransferDialogController implements DialogController {

    @FXML
    private TextField receiverUsernameField;
    @FXML
    private TextField amountField;

    private Client client;

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleTransfer() {
        String receiver = receiverUsernameField.getText();
        String amount = amountField.getText();

        if (receiver.isEmpty() || amount.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        // Logic to perform the transfer (placeholder)
        System.out.println("Transferring " + amount + " to " + receiver + " from client " + client.getUsername());
    }
}
