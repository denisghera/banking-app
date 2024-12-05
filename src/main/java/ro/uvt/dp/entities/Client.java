package ro.uvt.dp.entities;

import java.util.ArrayList;
import java.util.List;

import ro.uvt.dp.exceptions.LimitExceededException;
import ro.uvt.dp.services.Mediator;

public class Client {
	public static final int MAX_ACCOUNTS = 5;
	private String name;
	private String address;
	private Mediator mediator;
	private final List<Account> accounts;
	private final StringBuilder personalReport = new StringBuilder();

	public Client(String name, String address, List<Account> accounts) {
		this.name = name;
		this.address = address;
		this.accounts = new ArrayList<>(accounts);
	}

	public void addAccount(Account account) throws LimitExceededException {
		if (accounts.size() >= MAX_ACCOUNTS) {
			throw new LimitExceededException("Client cannot have more than " + MAX_ACCOUNTS + " accounts.");
		}
		accounts.add(account);
		personalReport.append("\tAccount ").append(account.getAccountCode()).append(" was created!\n");
	}
	public void removeAccount(String accountCode) {
		if (accounts.isEmpty()) {
			throw new IllegalStateException("Client doesn't have any accounts.");
		}
		Account accountToRemove = getAccount(accountCode);
		if (accountToRemove == null) {
			throw new IllegalArgumentException("Account with code " + accountCode + " not found.");
		}
		if(accountToRemove.amount > 0) {
			throw new IllegalStateException("Account still has money in it. Please retrieve the whole sum.");
		}
		personalReport.append("\tAccount ").append(accountCode).append(" was closed!\n");
		accounts.remove(accountToRemove);
	}
	public Account getAccount(String accountCode) {
		return accounts.stream()
				.filter(account -> account.getAccountCode().equals(accountCode))
				.findFirst()
				.orElse(null);
	}
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
	public void sendMessage(String message) {
		if (mediator != null) {
			mediator.sendMessage("[" + name + "] " + message, this);
		}
	}
	public void receiveMessage(String message) {
		System.out.println(name + " received: " + message);
	}
	public int getAccountsSize() {
		return accounts.size();
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getPersonalReport() {
		return personalReport.toString();
	}
	@Override
	public String toString() {
		return "\n\tClient [name=" + name + ", address=" + address + ", accounts=" + accounts + "]";
	}
}