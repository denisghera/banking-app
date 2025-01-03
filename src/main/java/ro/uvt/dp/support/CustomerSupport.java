package ro.uvt.dp.support;

import ro.uvt.dp.services.SupportHandler;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.accounts.states.ActiveAccountState;
import ro.uvt.dp.database.DatabaseConnector;

public class CustomerSupport implements SupportHandler {
    private SupportHandler nextHandler;

    public void setNextHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    public void handleRequest(Request request) {
        if(request.getPriority() == Request.Priority.BASIC) {
            Account account = request.getAccount();
            if (account != null) {
                account.setState(new ActiveAccountState());
                System.out.println("Customer Support: Account activated.");
                try {
                    DatabaseConnector.setAccountActive(account.getAccountCode(), true);
                } catch (Exception e) {
                    System.err.println("Customer Support: Failed to update the database: " + e.getMessage());
                }
            }
        } else if (nextHandler != null){
            nextHandler.handleRequest(request);
        }
    }
}
