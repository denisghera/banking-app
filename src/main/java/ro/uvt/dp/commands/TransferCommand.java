package ro.uvt.dp.commands;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.Command;

public class TransferCommand implements Command {
    private Account sourceAccount;
    private Account targetAccount;
    private double sum;

    public TransferCommand(Account sourceAccount, Account targetAccount, double sum) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.sum = sum;
    }

    @Override
    public void execute() throws InvalidAmountException, InsufficientFundsException {
        sourceAccount.transfer(targetAccount, sum);
    }
}
