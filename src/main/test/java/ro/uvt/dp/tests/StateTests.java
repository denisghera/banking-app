package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.accounts.AccountEURFactory;
import ro.uvt.dp.accounts.AccountRONFactory;
import ro.uvt.dp.accounts.states.ActiveAccountState;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountFactory;

import static org.junit.jupiter.api.Assertions.*;

public class StateTests {
    private AccountFactory ronFactory;
    private AccountFactory eurFactory;

    @BeforeEach
    public void setUp() {
        ronFactory = new AccountRONFactory();
        eurFactory = new AccountEURFactory();
    }
    @Test
    public void testDepositInActiveState() throws InvalidAmountException {
        Account account = ronFactory.create(100);
        account.setState(new ActiveAccountState());
        account.deposit(50);
        assertEquals(150 * 1.03, account.getTotalAmount());
    }
    @Test
    public void testDepositInClosedState() throws InvalidAmountException {
        Account account = eurFactory.create(100);
        assertThrows(IllegalStateException.class, () -> account.deposit(50));
    }

}
