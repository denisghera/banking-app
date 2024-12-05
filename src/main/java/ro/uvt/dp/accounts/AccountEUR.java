package ro.uvt.dp.accounts;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;

public class AccountEUR extends Account {

	public AccountEUR(double initialAmount) throws InvalidAmountException {
		super(initialAmount);
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
