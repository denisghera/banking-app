package ro.uvt.dp.support;

import ro.uvt.dp.entities.Account;
import ro.uvt.dp.services.SupportHandler;

public class AdminSupport implements SupportHandler {
    private SupportHandler nextHandler;

    public void setNextHandler(SupportHandler nextHandler) {
        // No next handler -> leave empty for now
    }
    public void handleRequest(Request request) {
        if(request.getPriority() == Request.Priority.CRITICAL) {
            Account account = request.getAccount();
            if (account != null) {
                System.out.println("Admin Support: Handling critical request for account: " + account.getAccountCode());
                // Perform additional actions if needed
            }
        } else if (nextHandler != null){
            System.out.println("Request cannot be handled!");
        }
    }
}
