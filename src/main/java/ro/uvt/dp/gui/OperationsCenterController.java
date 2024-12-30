package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.services.DialogController;

import java.io.IOException;

public class OperationsCenterController {

    @FXML
    private Button backButton;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleTransfer() {
        showDialog("Transfer Funds", "transferDialog.fxml");
    }

    @FXML
    private void handleDeposit() {
        showDialog("Deposit Funds", "depositDialog.fxml");
    }

    @FXML
    private void handleRetrieve() {
        showDialog("Retrieve Funds", "retrieveDialog.fxml");
    }

    @FXML
    private void handleCreateAccount() {
        showDialog("Create New Account", "accountCreation.fxml");
    }

    @FXML
    private void handleOptForRoundup() {
        showSimpleDialog("Opt for Roundup", "Lorem ipsum dolor sit amet.\n\nDo you agree?", () -> {
            System.out.println("Roundup agreed!");
        });
    }

    @FXML
    private void handleOptForInsurance() {
        showSimpleDialog("Opt for Insurance", "Lorem ipsum dolor sit amet.\n\nDo you agree?", () -> {
            System.out.println("Insurance agreed!");
        });
    }

    @FXML
    private void goBackToAccountDetails() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("accountDetails.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 420);

            AccountDetailsController controller = fxmlLoader.getController();
            controller.setClient(client);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Account Details");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(String title, String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(scene);

            Object controller = fxmlLoader.getController();
            if (controller instanceof DialogController) {
                ((DialogController) controller).setClient(client);
            }

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.err.println("Controller for " + fxmlFile + " does not implement DialogController.");
        }
    }
    private void showSimpleDialog(String title, String message, Runnable onAgree) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equalsIgnoreCase("OK") || response.getText().equalsIgnoreCase("Yes")) {
                onAgree.run();
            } else {
                System.out.println("Action canceled by user.");
            }
        });
    }
}
