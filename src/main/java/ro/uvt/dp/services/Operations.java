package ro.uvt.dp.services;

import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;

public interface Operations {
	double getTotalAmount();
	double getInterest();
	void deposit(double amount) throws InvalidAmountException;
	void retrieve(double amount) throws InvalidAmountException, InsufficientFundsException;
}
