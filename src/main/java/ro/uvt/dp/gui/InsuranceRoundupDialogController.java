package ro.uvt.dp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.entities.Account;

import java.util.ArrayList;
import java.util.List;

public class InsuranceRoundupDialogController {
    @FXML
    private VBox accountList;

    private Stage dialogStage;
    private List<Account> accounts = new ArrayList<>();
    private boolean isInsurance; // true for insurance, false for roundup

    public void setAccounts(List<Account> accounts, boolean isInsurance) {
        this.accounts = accounts;
        this.isInsurance = isInsurance;

        for (Account account : accounts) {
            CheckBox checkBox = new CheckBox(account.getAccountCode() + " - " + account.getAmount());
            checkBox.setUserData(account);
            accountList.getChildren().add(checkBox);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleConfirm() {
        List<Account> selectedAccounts = new ArrayList<>();
        for (var node : accountList.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    selectedAccounts.add((Account) checkBox.getUserData());
                }
            }
        }

        for (Account account : selectedAccounts) {
            if (isInsurance) {
                DatabaseConnector.setAccountInsurance(account.getAccountCode());
            } else {
                DatabaseConnector.setAccountRoundup(account.getAccountCode());
            }
        }

        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
