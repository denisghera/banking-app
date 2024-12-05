package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.accounts.AccountRON;
import ro.uvt.dp.commands.AccountOperationsInvoker;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    private Account account1;
    private Account account2;
    private AccountOperationsInvoker invoker;

    @BeforeEach
    public void setup() throws InvalidAmountException {
        account1 = new AccountRON(100);
        account2 = new AccountRON(50);

        invoker = new AccountOperationsInvoker();
    }
    @Test
    public void testDepositCommand() throws InvalidAmountException, InsufficientFundsException {
        account1.depositUsingCommand(invoker, 100);

        assertEquals(200 * 1.03, account1.getTotalAmount(), 0.001);
    }
    @Test
    public void testRetrieveCommand() throws InvalidAmountException, InsufficientFundsException {
        account1.retrieveUsingCommand(invoker, 50);

        assertEquals(50 * 1.03, account1.getTotalAmount(), 0.001);
    }
    @Test
    public void testTransferCommand() throws InvalidAmountException, InsufficientFundsException {
        account1.transferUsingCommand(invoker, account2,30);

        assertEquals(70 * 1.03, account1.getTotalAmount(), 0.001);
        assertEquals(80 * 1.03, account2.getTotalAmount(), 0.001);
    }
}
