package ro.uvt.dp.decorators;

import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.AccountDecorator;
import ro.uvt.dp.exceptions.InvalidAmountException;

public class RoundUpDecorator extends AccountDecorator {
    private double roundUpBalance = 0;

    public RoundUpDecorator(Account account) throws InvalidAmountException {
        super(account);
    }
    public double getRoundUpBalance() {
        return roundUpBalance;
    }
    public void addRoundUpBalance(double amount) throws InvalidAmountException {
        if (amount < 0) {
            throw new InvalidAmountException("Round-up balance cannot be negative.");
        }
        roundUpBalance += amount;
    }
    @Override
    public void transfer(Account targetAccount, double sum) {
        double roundUpDifference = Math.ceil(sum) - sum;

        if(roundUpDifference > 0) {
            account.retrieve(roundUpDifference);
            roundUpBalance += roundUpDifference;
            DatabaseConnector.updateRoundupBalanceInDatabase(account.getAccountCode(), roundUpBalance);
        }
        account.transfer(targetAccount, sum);
    }
    @Override
    public String toString() {
        return "Roundup [" + super.toString() + "]";
    }
}
