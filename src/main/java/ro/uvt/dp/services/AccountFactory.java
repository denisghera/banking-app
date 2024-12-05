package ro.uvt.dp.services;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;

public interface AccountFactory {
    Account create(double initialAmount) throws InvalidAmountException;
}
