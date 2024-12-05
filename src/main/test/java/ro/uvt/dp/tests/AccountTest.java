package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.accounts.AccountEURFactory;
import ro.uvt.dp.accounts.AccountRONFactory;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private AccountFactory ronFactory;
    private AccountFactory eurFactory;

    @BeforeEach
    public void setUp() {
        ronFactory = new AccountRONFactory();
        eurFactory = new AccountEURFactory();
    }
    @Test
    public void testCreateRONAccount() throws InvalidAmountException {
        Account accountRON = ronFactory.create(800);
        assertNotNull(accountRON);
        assertEquals(864, accountRON.getTotalAmount());
    }
    @Test
    public void testCreateEURAccount() throws InvalidAmountException {
        Account accountEUR = eurFactory.create(200);
        assertNotNull(accountEUR);
        assertEquals(202, accountEUR.getTotalAmount());
    }
    @Test
    public void testCreateAccountWithNegativeAmount() {
        assertThrows(InvalidAmountException.class, () -> {
            ronFactory.create(-50);
        });
    }
    @Test
    public void testDeposit() throws InvalidAmountException {
        Account account = ronFactory.create(100);
        account.deposit(200);
        assertEquals(309, account.getTotalAmount());
    }
    @Test
    public void testDepositNegativeAmount()  throws InvalidAmountException{
        Account account = ronFactory.create(100);
        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(-50);
        });
    }

    @Test
    public void testRetrieve() throws InvalidAmountException, InsufficientFundsException {
        Account account = eurFactory.create(200);
        account.retrieve(100);
        assertEquals(101, account.getTotalAmount());
    }
    @Test
    public void testRetrieveNegativeAmount() throws InvalidAmountException{
        Account account = eurFactory.create(200);
        assertThrows(InvalidAmountException.class, () -> {
            account.retrieve(-50);
        });
    }
    @Test
    public void testRetrieveMoreFunds() throws InvalidAmountException {
        Account account = eurFactory.create(200);
        assertThrows(InsufficientFundsException.class, () -> {
            account.retrieve(300);
        });
    }
    @Test
    public void testTransfer() throws InvalidAmountException, InsufficientFundsException {
        Account account1 = ronFactory.create(100);
        Account account2 = ronFactory.create(300);
        account1.transfer(account2, 50);
        assertEquals(51.5, account1.getTotalAmount());
        assertEquals(360.5, account2.getTotalAmount());

    }
    @Test
    public void testTransferToInvalidAccount() throws InvalidAmountException {
        Account account1 = ronFactory.create(100);
        assertThrows(IllegalArgumentException.class, () -> {
            account1.transfer(null, 10);
        });
    }
    @Test
    public void testTransferNegativeAmount() throws InvalidAmountException {
        Account account1 = ronFactory.create(100);
        Account account2 = ronFactory.create(300);
        assertThrows(InvalidAmountException.class, () -> {
            account1.transfer(account2,-50);
        });
    }
    @Test
    public void testTransferMoreFunds() throws InvalidAmountException {
        Account account1 = ronFactory.create(100);
        Account account2 = ronFactory.create(300);
        assertThrows(InsufficientFundsException.class, () -> {
            account1.transfer(account2,200);
        });
    }
    @Test
    public void testTransferToDifferentAccountType() throws InvalidAmountException {
        Account account1 = ronFactory.create(100);
        Account account2 = eurFactory.create(400);
        assertThrows(IllegalArgumentException.class, () -> {
            account1.transfer(account2, 10);
        });
    }
}
