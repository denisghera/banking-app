package ro.uvt.dp.decorators;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.AccountDecorator;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;

public class RoundUpDecorator extends AccountDecorator {
    private double roundUpBalance = 0;

    public RoundUpDecorator(Account account) throws InvalidAmountException {
        super(account);
    }
    public double getRoundUpBalance() {
        return roundUpBalance;
    }

    @Override
    public void transfer(Account targetAccount, double sum) throws InvalidAmountException, InsufficientFundsException {
        account.transfer(targetAccount, sum);

        double roundUpDifference = Math.ceil(sum) - sum;

        if(roundUpDifference > 0) {
            account.retrieve(roundUpDifference);
            roundUpBalance += roundUpDifference;
        }
    }
}
