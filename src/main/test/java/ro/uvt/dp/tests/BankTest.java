package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Bank;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.exceptions.LimitExceededException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static ro.uvt.dp.entities.Account.TYPE.EUR;
import static ro.uvt.dp.entities.Account.TYPE.RON;

public class BankTest {

    private Bank bank;

    @BeforeEach
    public void setup() {
        bank = Bank.getInstance("BANK001");
        bank.resetBank();
    }

    @Test
    public void testSingleton() throws Exception {
        Bank anotherInstance = Bank.getInstance();
        assertSame(bank, anotherInstance);
        assertEquals("BANK001", bank.getBankCode());
    }

    @Test
    public void testCreateClient() throws LimitExceededException {
        Client client = bank.createClient("Alice", "Wonderland Ave", new ArrayList<>());
        assertNotNull(client);
        assertEquals("Alice", client.getName());
        assertEquals("Wonderland Ave", client.getAddress());
    }

    @Test
    public void testClientLimit() throws LimitExceededException {
        for (int i = 0; i < Bank.MAX_CLIENTS_NUMBER; i++) {
            bank.createClient("Client" + i, "Address" + i, new ArrayList<>());
        }

        assertThrows(LimitExceededException.class, () -> bank.createClient("ExtraClient", "ExtraAddress", new ArrayList<>()));
    }

    @Test
    public void testCreateAccount() throws InvalidAmountException, LimitExceededException {
        Client client = bank.createClient("Bob", "Elm St", new ArrayList<>());
        Account account = bank.createAccount(RON, 100.0, client);

        assertNotNull(account);
        assertEquals(103.0, account.getTotalAmount());
        assertEquals(client.getAccountsSize(), 1);
    }
    @Test
    public void testCreateInvalidAccountType() throws LimitExceededException {
        Client client = bank.createClient("Bob", "Elm St", new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount(null, 50.0, client));
    }

    @Test
    public void testGenerateDailyReport() throws LimitExceededException, InvalidAmountException, InsufficientFundsException {
        Client client1 = bank.createClient("Dave", "Oak Rd", new ArrayList<>());
        Client client2 = bank.createClient("Eve", "Maple Dr", new ArrayList<>());

        Account account1 = bank.createAccount(EUR, 200.0, client1);
        Account account2 = bank.createAccount(RON, 300.0, client2);

        account1.retrieve(200);
        client1.removeAccount(account1.getAccountCode());

        String report = bank.generateDailyReport();
        System.out.println(report);

        String client1Report = "Actions for client Dave:\n" + "\tAccount " + account1.getAccountCode() + " was created!\n";
        client1Report += "\tAccount " + account1.getAccountCode() + " was closed!";
        String client2Report = "Actions for client Eve:\n" + "\tAccount " + account2.getAccountCode() + " was created!";

        assertTrue(report.contains(client1Report));
        assertTrue(report.contains(client2Report));
    }
}
