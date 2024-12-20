package ro.uvt.dp.accounts.states;

import ro.uvt.dp.services.AccountState;

public class ClosedAccountState implements AccountState {
    @Override
    public String handleRequest() {
        return "ERROR: Account is closed. No operations are allowed.";
    }
}
