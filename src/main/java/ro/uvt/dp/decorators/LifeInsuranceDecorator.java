package ro.uvt.dp.decorators;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.AccountDecorator;
import ro.uvt.dp.exceptions.InvalidAmountException;

public class LifeInsuranceDecorator extends AccountDecorator {

    private boolean isInsured;
    private float maxInsurance;
    public LifeInsuranceDecorator(Account account) throws InvalidAmountException {
        super(account);
        updateLifeInsurance(false, 0);
    }
    public boolean getInsured() {
        return isInsured;
    }
    public float getMaxInsurance() {
        return maxInsurance;
    }

    public void updateLifeInsurance(boolean insured, float maxInsured) throws InvalidAmountException {
        if (maxInsured < 0) {
            throw new InvalidAmountException("Maximum insurance amount cannot be negative.");
        }
        isInsured = insured;
        maxInsurance = maxInsured;
    }
}
