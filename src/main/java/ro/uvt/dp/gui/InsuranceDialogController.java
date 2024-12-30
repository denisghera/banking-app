package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.services.DialogController;

public class InsuranceDialogController implements DialogController {

    private Client client;

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleAgree() {
        System.out.println("Insurance option agreed by client: " + client.getUsername());
    }
}
