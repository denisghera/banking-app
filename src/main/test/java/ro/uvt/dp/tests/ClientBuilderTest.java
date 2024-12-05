package ro.uvt.dp.tests;

import org.junit.jupiter.api.Test;
import ro.uvt.dp.accounts.AccountRON;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.entities.ClientBuilder;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.exceptions.LimitExceededException;

import static org.junit.jupiter.api.Assertions.*;

public class ClientBuilderTest {

    @Test
    public void testBuild() {
        Client client = new ClientBuilder().setName("John Doe").setAddress("123 Elm St").build();

        assertEquals("John Doe", client.getName());
        assertEquals("123 Elm St", client.getAddress());
        assertEquals(0, client.getAccountsSize());
    }

    @Test
    public void testAccountAdding() throws LimitExceededException, InvalidAmountException {
        Account account1 = new AccountRON(100);
        Account account2 = new AccountRON(200);

        Client client = new ClientBuilder().setName("John Doe").setAddress("123 Elm St")
                .addAccount(account1)
                .addAccount(account2)
                .build();

        assertEquals(2, client.getAccountsSize());
        assertEquals(account1, client.getAccount(account1.getAccountCode()));
        assertEquals(account2, client.getAccount(account2.getAccountCode()));
    }

    @Test
    public void testAccountLimit() throws LimitExceededException, InvalidAmountException {
        ClientBuilder builder = new ClientBuilder();
        builder.setName("John Doe").setAddress("123 Elm St");

        for (int i = 0; i < Client.MAX_ACCOUNTS; i++) {
            builder.addAccount(new AccountRON(100));
        }
        assertThrows(LimitExceededException.class, () -> builder.addAccount(new AccountRON(100)));
    }

    @Test
    public void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> new ClientBuilder().setName("John Doe").setAddress(null));
        assertThrows(IllegalArgumentException.class, () -> new ClientBuilder().setName(null).setAddress("123 Elm St"));
        assertThrows(IllegalArgumentException.class, () -> new ClientBuilder().setName("").setAddress("123 Elm St"));
        assertThrows(IllegalArgumentException.class, () -> new ClientBuilder().setName("John Doe").setAddress(""));
    }
}
