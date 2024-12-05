package ro.uvt.dp.commands;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.Command;

public class DepositCommand implements Command {
    private Account account;
    private double sum;

    public DepositCommand(Account account, double sum) {
        this.account = account;
        this.sum = sum;
    }

    @Override
    public void execute() throws InvalidAmountException {
        account.deposit(sum);
    }
}

