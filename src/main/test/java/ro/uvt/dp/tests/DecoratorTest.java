package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.accounts.AccountRON;
import ro.uvt.dp.decorators.LifeInsuranceDecorator;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.decorators.RoundUpDecorator;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;

public class DecoratorTest {

    private Account senderAccount;
    private Account receiverAccount;
    private RoundUpDecorator roundUpAccount;
    private LifeInsuranceDecorator lifeInsuranceAccount;

    @BeforeEach
    public void setup() throws InvalidAmountException {
        senderAccount = new AccountRON(100);
        receiverAccount = new AccountRON(50);

        roundUpAccount = new RoundUpDecorator(senderAccount);
        lifeInsuranceAccount = new LifeInsuranceDecorator(receiverAccount);
    }
    @Test
    public void testRoundUpTransfer() throws InvalidAmountException, InsufficientFundsException {
        double transferAmount = 10.2;
        roundUpAccount.transfer(receiverAccount, transferAmount);

        assertEquals(0.8, roundUpAccount.getRoundUpBalance(), 0.001);
        assertEquals(89 * 1.03, senderAccount.getTotalAmount(), 0.001);
        assertEquals(60.2 * 1.03, receiverAccount.getTotalAmount(), 0.001);
    }

    @Test
    public void testLifeInsurance() throws InvalidAmountException {
        assertEquals(0, lifeInsuranceAccount.getMaxInsurance());
        assertFalse(lifeInsuranceAccount.getInsured());

        lifeInsuranceAccount.updateLifeInsurance(true, 1000);
        assertEquals(1000, lifeInsuranceAccount.getMaxInsurance());
        assertTrue(lifeInsuranceAccount.getInsured());
    }
}
