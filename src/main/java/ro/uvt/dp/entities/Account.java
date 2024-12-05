package ro.uvt.dp.entities;

import ro.uvt.dp.commands.AccountOperationsInvoker;
import ro.uvt.dp.commands.DepositCommand;
import ro.uvt.dp.commands.RetrieveCommand;
import ro.uvt.dp.commands.TransferCommand;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.services.Command;
import ro.uvt.dp.services.Operations;
import ro.uvt.dp.services.Transfer;

import java.util.UUID;

public abstract class Account implements Operations, Transfer {
	protected String accountCode;
	protected double amount = 0;
	private Client client;
	public enum TYPE {
		EUR, RON
	};

	protected Account(double initialAmount) throws InvalidAmountException {
		this.accountCode = UUID.randomUUID().toString();
		deposit(initialAmount);
	}
	@Override
	public double getTotalAmount() {
		return amount + amount * getInterest();
	}
	@Override
	public void deposit(double sum) throws InvalidAmountException {
		if (sum <= 0) {
			throw new InvalidAmountException("Cannot deposit a negative or zero sum.");
		}
		this.amount += sum;
	}
	@Override
	public void retrieve(double sum) throws InvalidAmountException, InsufficientFundsException {
		if (sum <= 0) {
			throw new InvalidAmountException("Cannot retrieve a negative or zero sum.");
		}
		if (this.amount < sum) {
			throw new InsufficientFundsException("Insufficient funds.");
		}
		this.amount -= sum;
	}
	@Override
	public double getInterest() {
        return 0;
    }
	@Override
	public void transfer(Account targetAccount, double sum) throws InsufficientFundsException, InvalidAmountException {
		if (targetAccount == null) {
			throw new IllegalArgumentException("Invalid transfer details.");
		}
		if (sum <= 0 ) {
			throw new InvalidAmountException("Sum should be greater than 0.");
		}
		if (this.amount < sum) {
			throw new InsufficientFundsException("Insufficient funds for transfer.");
		}
		if(targetAccount.getClass() != this.getClass()) {
			throw new IllegalArgumentException("Accounts must be of the same type");
		}
		this.retrieve(sum);
		targetAccount.deposit(sum);
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void depositUsingCommand(AccountOperationsInvoker invoker, double sum) throws InvalidAmountException, InsufficientFundsException {
		Command depositCommand = new DepositCommand(this, sum);
		invoker.invokeCommand(depositCommand);
	}
	public void retrieveUsingCommand(AccountOperationsInvoker invoker, double sum) throws InvalidAmountException, InsufficientFundsException {
		Command retrieveCommand = new RetrieveCommand(this, sum);
		invoker.invokeCommand(retrieveCommand);
	}
	public void transferUsingCommand(AccountOperationsInvoker invoker, Account targetAccount, double sum) throws InvalidAmountException, InsufficientFundsException {
		Command transferCommand = new TransferCommand(this, targetAccount, sum);
		invoker.invokeCommand(transferCommand);
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Client getClient() {
		return client;
	}
	@Override
	public String toString() {
		return "code=" + accountCode + ", amount=" + this.getTotalAmount();
	}
}