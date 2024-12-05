package ro.uvt.dp.entities;

import ro.uvt.dp.exceptions.InvalidAmountException;

public abstract class AccountDecorator extends Account {
    protected Account account;
    public AccountDecorator(Account account) throws InvalidAmountException {
        super(account.amount);
        this.account = account;
    }
}
