package ro.uvt.dp.entities;

import ro.uvt.dp.accounts.states.ClosedAccountState;
import ro.uvt.dp.commands.AccountOperationsInvoker;
import ro.uvt.dp.commands.DepositCommand;
import ro.uvt.dp.commands.RetrieveCommand;
import ro.uvt.dp.commands.TransferCommand;
import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.memento.AccountMemento;
import ro.uvt.dp.memento.History;
import ro.uvt.dp.services.*;
import ro.uvt.dp.support.Request;

import java.util.UUID;

public abstract class Account implements Operations, Transfer {
	public boolean isTransferOperation = false;
	private final History history = new History();
	protected String accountCode;
	protected double amount = 0;
	private Client client;
	private AccountState state;
	public enum TYPE {
		EUR, RON
	};
	protected Account(double initialAmount) throws InvalidAmountException {
		this(UUID.randomUUID().toString().substring(0, 5), initialAmount, new ClosedAccountState());
	}
	protected Account(String accountCode, double initialAmount, AccountState state) throws InvalidAmountException {
		this.accountCode = accountCode;
		setState(state);
		if (state instanceof ClosedAccountState) {
			System.out.println("Generating request for activation of account...");
			Request request = new Request(Request.Priority.NORMAL, this.getAccountCode(), "Account activation for account " + this.getAccountCode());
			DatabaseConnector.createTicket(request);
		}
		this.amount = initialAmount;
	}
	@Override
	public double getTotalAmount() {
		return amount + amount * getInterest();
	}
	public double getAmount() {
		return amount;
	}
	@Override
	public void deposit(double sum) {
		history.saveState(this.saveState());
		try {
			if (this.request().contains("ERROR")) {
				throw new IllegalStateException("Cannot deposit into a closed account.");
			}
			if (sum <= 0) {
				throw new InvalidAmountException("Cannot deposit a negative or zero sum.");
			}
			if (sum >= 1000 && !isTransferOperation) {
				System.out.println("Generating request for large deposit...");
				Request request = new Request(Request.Priority.CRITICAL, this.getAccountCode(), "Request for large deposit, sum: " + sum);
				DatabaseConnector.createTicket(request);
				DatabaseConnector.saveTransaction("deposit", this.getAccountCode(), "", sum, "approval");
				return;
			}
			this.amount += sum;
			DatabaseConnector.updateDatabaseOnOperation(this);
			if (!isTransferOperation)
				DatabaseConnector.saveTransaction("deposit", this.getAccountCode(), "", sum, "success");
		} catch (Exception e) {
			this.restoreState(history.getLastSavedState());
			System.out.println("Deposit failed, rolling back: " + e.getMessage());
			throw new RuntimeException("Deposit failed: " + e.getMessage());
		}
	}
	@Override
	public void retrieve(double sum) {
		history.saveState(this.saveState());
		try {
			if (this.request().contains("ERROR")) {
				throw new IllegalStateException("Cannot retrieve from a closed account.");
			}
			if (sum <= 0) {
				throw new InvalidAmountException("Cannot retrieve a negative or zero sum.");
			}
			if (this.amount < sum) {
				throw new InsufficientFundsException("Insufficient funds.");
			}
			this.amount -= sum;
			DatabaseConnector.updateDatabaseOnOperation(this);
			if (!isTransferOperation)
				DatabaseConnector.saveTransaction("retrieve", this.getAccountCode(), "", sum, "success");
		} catch (Exception e) {
			this.restoreState(history.getLastSavedState());
			System.out.println("Retrieve failed, rolling back: " + e.getMessage());
			throw new RuntimeException("Retrieve failed: " + e.getMessage());
		}
	}
	@Override
	public double getInterest() {
        return 0;
    }
	@Override
	public void transfer(Account targetAccount, double sum) {
		history.saveState(this.saveState());
		try {
			if (this.request().contains("ERROR")) {
				throw new IllegalStateException("Cannot transfer from a closed account.");
			}
			if (targetAccount == null) {
				throw new IllegalArgumentException("Invalid transfer details.");
			}
			if (targetAccount.request().contains("ERROR")) {
				throw new IllegalStateException("Cannot transfer to a closed account.");
			}
			if (sum <= 0) {
				throw new InvalidAmountException("Sum should be greater than 0.");
			}
			if (this.amount < sum) {
				throw new InsufficientFundsException("Insufficient funds for transfer.");
			}
			if (getBaseClass(this) != getBaseClass(targetAccount)) {
				throw new IllegalArgumentException("Accounts must be of the same type");
			}
			isTransferOperation = true;
			this.retrieve(sum);
			isTransferOperation = false;
			targetAccount.isTransferOperation = true;
			targetAccount.deposit(sum);
			targetAccount.isTransferOperation = false;
			DatabaseConnector.saveTransaction("transfer", this.getAccountCode(), targetAccount.getAccountCode(), sum, "success");
		} catch (Exception e) {
			this.restoreState(history.getLastSavedState());
			System.out.println("Transfer failed, rolling back: " + e.getMessage());
			throw new RuntimeException("Transfer failed: " + e.getMessage());
		}
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void depositUsingCommand(AccountOperationsInvoker invoker, double sum) {
		Command depositCommand = new DepositCommand(this, sum);
		invoker.invokeCommand(depositCommand);
	}
	public void retrieveUsingCommand(AccountOperationsInvoker invoker, double sum) {
		Command retrieveCommand = new RetrieveCommand(this, sum);
		invoker.invokeCommand(retrieveCommand);
	}
	public void transferUsingCommand(AccountOperationsInvoker invoker, Account targetAccount, double sum) {
		Command transferCommand = new TransferCommand(this, targetAccount, sum);
		invoker.invokeCommand(transferCommand);
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Client getClient() {
		return client;
	}
	public void setState(AccountState state) {
		this.state = state;
	}
	public AccountState getState() {
		return this.state;
	}
	public String request() {
		return state.handleRequest();
	}
	public AccountMemento saveState() {
		return new AccountMemento(this.amount, this.state);
	}
	public void restoreState(AccountMemento memento) {
		if(memento != null) {
			this.amount = memento.getAmount();
			setState(memento.getState());
		}
	}
	private Class<?> getBaseClass(Account account) {
		while (account instanceof AccountDecorator) {
			account = ((AccountDecorator) account).getAccount();
		}
		return account.getClass();
	}
	@Override
	public String toString() {
		return "code=" + accountCode + ", amount=" + this.getAmount();
	}
}