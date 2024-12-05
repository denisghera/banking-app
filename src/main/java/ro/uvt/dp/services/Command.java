package ro.uvt.dp.services;

import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;

public interface Command {
    void execute() throws InvalidAmountException, InsufficientFundsException;
}
