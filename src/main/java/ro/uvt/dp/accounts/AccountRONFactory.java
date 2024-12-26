package ro.uvt.dp.accounts;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountFactory;

public class AccountRONFactory implements AccountFactory {
    @Override
    public Account create(double initialAmount) throws InvalidAmountException {
        return new AccountRON(initialAmount);
    }
    public Account create(String accountCode, double initialAmount) throws InvalidAmountException {
        return new AccountRON(accountCode, initialAmount);
    }
}
