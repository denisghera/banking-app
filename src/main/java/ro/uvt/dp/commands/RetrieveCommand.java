package ro.uvt.dp.commands;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.Command;

public class RetrieveCommand implements Command {
    private Account account;
    private double sum;

    public RetrieveCommand(Account account, double sum) {
        this.account = account;
        this.sum = sum;
    }

    @Override
    public void execute() throws InvalidAmountException, InsufficientFundsException {
        account.retrieve(sum);
    }
}

