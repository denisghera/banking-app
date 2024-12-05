package ro.uvt.dp.services;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;

public interface Transfer {
	void transfer(Account c, double s) throws InsufficientFundsException, InvalidAmountException;
}
