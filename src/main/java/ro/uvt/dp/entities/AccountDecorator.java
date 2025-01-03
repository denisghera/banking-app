package ro.uvt.dp.entities;

import ro.uvt.dp.exceptions.InvalidAmountException;

public abstract class AccountDecorator extends Account {
    protected Account account;
    public AccountDecorator(Account account) throws InvalidAmountException {
        super(account.getAccountCode(), account.getAmount(), account.getState());
        this.account = account;
    }
    @Override
    public String toString() {
        return account.toString();
    }
    public Account getBaseAccount() {
        if (account instanceof AccountDecorator) {
            return ((AccountDecorator) account).getBaseAccount();
        }
        return account;
    }
    public Account getAccount() {
        return account;
    }
}
