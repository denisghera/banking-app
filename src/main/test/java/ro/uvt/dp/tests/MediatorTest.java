package ro.uvt.dp.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uvt.dp.entities.Bank;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.LimitExceededException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MediatorTest {

    private Bank bank;
    private Client client1;
    private Client client2;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() throws LimitExceededException {
        System.setOut(new PrintStream(outContent));

        bank = Bank.getInstance("ABC");
        bank.resetBank();

        client1 = bank.createClient("John", "1st Street", new ArrayList<>());
        client2 = bank.createClient("Mary", "2nd Street", new ArrayList<>());
    }

    @Test
    public void testClientCommunication() {
        client1.sendMessage("Hello from John");
        client2.sendMessage("Hi John! I am Mary.");

        String consoleOutput = outContent.toString().trim();
        assertEquals("Mary received: [John] Hello from John\r\nJohn received: [Mary] Hi John! I am Mary.", consoleOutput);
    }

    @Test
    public void testBankToClientCommunication() {
        bank.sendMessageToClient("Admin speaking to you...", client1);

        String consoleOutput = outContent.toString().trim();
        assertEquals("John received: [Bank] Admin speaking to you...", consoleOutput);
    }

    @Test
    public void testBankToEveryoneCommunication() {
        bank.sendMessageToAll("This is the bank, all clients must respond.");

        String consoleOutput = outContent.toString().trim();
        String expectedOutput = "John received: [Bank] This is the bank, all clients must respond.\r\n";
        expectedOutput += "Mary received: [Bank] This is the bank, all clients must respond.";
        assertEquals(expectedOutput, consoleOutput);
    }
}