package ro.uvt.dp.commands;

import ro.uvt.dp.exceptions.InsufficientFundsException;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.Command;

import java.util.Stack;

public class AccountOperationsInvoker {
    private Stack<Command> commandHistory = new Stack<>();

    public void invokeCommand(Command command) throws InvalidAmountException, InsufficientFundsException {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
        }
    }
    public Stack<Command> getCommandHistory() {
        return commandHistory;
    }
}

