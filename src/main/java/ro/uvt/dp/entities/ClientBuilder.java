package ro.uvt.dp.entities;

import ro.uvt.dp.exceptions.LimitExceededException;
import ro.uvt.dp.services.ClientBuilderInterface;

import java.util.ArrayList;
import java.util.List;

public class ClientBuilder implements ClientBuilderInterface {
    private String username;
    private String name;
    private String address;
    private String email;
    private String bankID;
    private final List<Account> accounts = new ArrayList<>();

    @Override
    public ClientBuilderInterface setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        return this;
    }

    @Override
    public ClientBuilderInterface setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        this.address = address;
        return this;
    }

    public ClientBuilderInterface setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
        return this;
    }

    public ClientBuilderInterface setBankID(String bankID) {
        if (bankID == null || bankID.isEmpty()) {
            throw new IllegalArgumentException("BankID cannot be null or empty");
        }
        this.bankID = bankID;
        return this;
    }

    public ClientBuilderInterface setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
        return this;
    }

    @Override
    public ClientBuilderInterface addAccount(Account account) throws LimitExceededException {
        if (this.accounts.size() >= Client.MAX_ACCOUNTS) {
            throw new LimitExceededException("Client cannot have more than " + Client.MAX_ACCOUNTS + " accounts.");
        }
        this.accounts.add(account);
        return this;
    }

    @Override
    public Client build() {
        if (this.username == null || this.name == null || this.address == null || this.email == null || this.bankID == null) {
            throw new IllegalStateException("Client username, name, address, email, or bankID not specified!");
        }

        return new Client(username, name, address, email, bankID, new ArrayList<>(accounts));
    }
}
