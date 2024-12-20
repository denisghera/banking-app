package ro.uvt.dp.accounts.states;

import ro.uvt.dp.services.AccountState;

public class ActiveAccountState implements AccountState {
    @Override
    public String handleRequest() {
        return "SUCCESS: Account is active and ready for operations.";
    }
}
