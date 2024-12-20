package ro.uvt.dp.support;

import ro.uvt.dp.services.SupportHandler;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.accounts.states.ActiveAccountState;

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
            }
        } else if (nextHandler != null){
            nextHandler.handleRequest(request);
        }
    }
}
