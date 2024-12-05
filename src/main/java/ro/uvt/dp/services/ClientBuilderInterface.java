package ro.uvt.dp.services;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.entities.Client;
import ro.uvt.dp.exceptions.LimitExceededException;

public interface ClientBuilderInterface {
    ClientBuilderInterface setName(String name);
    ClientBuilderInterface setAddress(String address);
    ClientBuilderInterface addAccount(Account account) throws LimitExceededException;
    Client build();
}
