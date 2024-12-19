package ro.uvt.dp.services;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.LimitExceededException;

public interface ClientBuilderInterface {
    ClientBuilderInterface setUsername(String username);
    ClientBuilderInterface setEmail(String email);
    ClientBuilderInterface setName(String name);
    ClientBuilderInterface setAddress(String address);
    ClientBuilderInterface setBankID(String bankID);
    ClientBuilderInterface addAccount(Account account) throws LimitExceededException;
    Client build();
}
