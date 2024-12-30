package ro.uvt.dp.accounts;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountFactory;
import ro.uvt.dp.services.AccountState;

public class AccountEURFactory implements AccountFactory {
    @Override
    public Account create(double initialAmount) throws InvalidAmountException {
        return new AccountEUR(initialAmount);
    }
    public Account create(String accountCode, double initialAmount, AccountState state) throws InvalidAmountException {
        return new AccountEUR(accountCode, initialAmount, state);
    }
}
