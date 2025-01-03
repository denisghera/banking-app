package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private void handleOptForRoundup() {
        showAccountSelectionDialog("Opt for Roundup", false);
    }

    @FXML
    private void handleOptForInsurance() {
        showAccountSelectionDialog("Opt for Insurance", true);
    }

    private void showAccountSelectionDialog(String title, boolean isInsurance) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("insuranceRoundupDialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            InsuranceRoundupDialogController controller = fxmlLoader.getController();
            controller.setAccounts(client.getAccounts(), isInsurance);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(scene);
            controller.setDialogStage(dialog);

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Scene scene = new Scene(fxmlLoader.load());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(scene);

            Object controller = fxmlLoader.getController();

            if (controller instanceof DialogController) {
                ((DialogController) controller).setClient(client);

                if (controller instanceof DepositDialogController) {
                    ((DepositDialogController) controller).setDialogStage(dialog);
                }
                else if (controller instanceof RetrieveDialogController) {
                    ((RetrieveDialogController) controller).setDialogStage(dialog);
                }
                else if (controller instanceof TransferDialogController) {
                    ((TransferDialogController) controller).setDialogStage(dialog);
                }
            }

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
