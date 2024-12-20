package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.accounts.AccountEURFactory;
import ro.uvt.dp.accounts.AccountRONFactory;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.SupportHandler;
import ro.uvt.dp.support.CustomerSupport;
import ro.uvt.dp.support.Request;

import static org.junit.jupiter.api.Assertions.*;

public class CorTests {
    AccountRONFactory ronFactory;
    AccountEURFactory eurFactory;
    @BeforeEach
    public void setUp() {
        ronFactory = new AccountRONFactory();
        eurFactory = new AccountEURFactory();
    }
    @Test
    public void testSmallDepositActivation() throws InvalidAmountException {
        Account account = ronFactory.create(100);
        assertTrue(account.request().contains("SUCCESS"), "Account should be activated.");
    }
    @Test
    public void testLargeDepositRequest() throws InvalidAmountException {
        Account account = ronFactory.create(500);
        account.deposit(1500);
        assertEquals(2000 * 1.08, account.getTotalAmount(), "Amount should reflect deposit.");
    }
}
