package ro.uvt.dp.accounts;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountState;

public class AccountEUR extends Account {

	public AccountEUR(double initialAmount) throws InvalidAmountException {
		super(initialAmount);
	}
	public AccountEUR(String accountCode, double initialAmount, AccountState state) throws InvalidAmountException {
		super(accountCode, initialAmount, state);
	}
	@Override
	public double getInterest() {
		return 0.01;
	}
	@Override
	public String toString() {
		return "\n\t\tAccount EUR [" + super.toString() + "]";
	}
}
