package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.accounts.AccountRON;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.exceptions.LimitExceededException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {

    private Client client;

    @BeforeEach
    public void setup() {
        client = new Client("Alice", "Wonderland Ave", new ArrayList<>());
    }

    @Test
    public void testAddAccount() throws LimitExceededException, InvalidAmountException {
        Account account = new AccountRON(100);
        client.addAccount(account);

        assertEquals(1, client.getAccountsSize());
    }
    @Test
    public void testAddAccountLimit() throws LimitExceededException, InvalidAmountException {
        for (int i = 0; i < Client.MAX_ACCOUNTS; i++) {
            Account accountNew = new AccountRON(100);
            client.addAccount(accountNew);
        }
        assertThrows(LimitExceededException.class, () -> client.addAccount(new AccountRON(40)));
    }
    @Test
    public void testRemoveAccount() throws LimitExceededException, InvalidAmountException, InsufficientFundsException {
        Account account = new AccountRON(30);
        client.addAccount(account);
        account.retrieve(30);
        client.removeAccount(account.getAccountCode());
        assertEquals(0, client.getAccountsSize());
    }
    @Test
    public void testRemoveNonExistentAccount() throws LimitExceededException, InvalidAmountException {
        Account account2 = new AccountRON(30);
        client.addAccount(account2);
        assertThrows(IllegalArgumentException.class, () -> client.removeAccount("NONEXISTENT"));
    }
    @Test
    public void testRemoveAccountWithNonZeroBalance() throws LimitExceededException, InvalidAmountException {
        Account accountNew = new AccountRON(50);
        client.addAccount(accountNew);
        assertThrows(IllegalStateException.class, () -> client.removeAccount(accountNew.getAccountCode()));
    }
    @Test
    public void testGetAccount() throws LimitExceededException, InvalidAmountException {
        Account account = new AccountRON(100);
        client.addAccount(account);
        Account retrievedAccount = client.getAccount(account.getAccountCode());

        assertNotNull(retrievedAccount);
        assertEquals(account.getAccountCode(), retrievedAccount.getAccountCode());
    }
    @Test
    public void testGetNonExistentAccount() {
        assertNull(client.getAccount("NONEXISTENT"));
    }
    @Test
    public void testGetPersonalReport() throws LimitExceededException, InvalidAmountException, InsufficientFundsException {
        Account account1 = new AccountRON(100.0);
        client.addAccount(account1);

        account1.retrieve(100);
        client.removeAccount(account1.getAccountCode());

        String report = client.getPersonalReport();

        assertTrue(report.contains("was created!"));
        assertTrue(report.contains("was closed!"));
    }
}
