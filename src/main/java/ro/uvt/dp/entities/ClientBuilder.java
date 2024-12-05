package ro.uvt.dp.entities;

import ro.uvt.dp.exceptions.LimitExceededException;
import ro.uvt.dp.services.ClientBuilderInterface;

import java.util.ArrayList;
import java.util.List;

public class ClientBuilder implements ClientBuilderInterface {
    private String name;
    private String address;
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
        if (this.name == null || this.address == null) {
            throw new IllegalStateException("Client name or address not specified!");
        }
        return new Client(name, address, new ArrayList<>(accounts));
    }
}
