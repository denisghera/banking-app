package ro.uvt.dp.accounts;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountState;

public class AccountRON extends Account {

	public AccountRON(double initialAmount) throws InvalidAmountException {
		super(initialAmount);
	}
	public AccountRON(String accountCode, double initialAmount, AccountState state) throws InvalidAmountException {
		super(accountCode, initialAmount, state);
	}
	@Override
	public double getInterest() {
		if (amount < 500)
			return 0.03;
		else
			return 0.08;
	}
	@Override
	public String toString() {
		return "\n\t\tAccount RON [" + super.toString() + "]";
	}
}
