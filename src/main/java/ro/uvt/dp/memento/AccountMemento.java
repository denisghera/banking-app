package ro.uvt.dp.memento;

import ro.uvt.dp.services.AccountState;

public class AccountMemento {
    private final double amount;
    private final AccountState state;

    public AccountMemento(double amount, AccountState state) {
        this.amount = amount;
        this.state = state;
    }
    public double getAmount() {
        return this.amount;
    }
    public AccountState getState() {
        return this.state;
    }
}
