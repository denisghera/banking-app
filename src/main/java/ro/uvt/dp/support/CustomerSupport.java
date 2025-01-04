package ro.uvt.dp.support;

import ro.uvt.dp.services.SupportHandler;
import ro.uvt.dp.database.DatabaseConnector;

public class CustomerSupport implements SupportHandler {
    private SupportHandler nextHandler;

    public void setNextHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public boolean handleRequest(Request request) {
        if (request.getPriority() == Request.Priority.NORMAL) {
            String message = request.getMessage();

            if (message != null && message.toLowerCase().contains("account activation")) {
                String accountId = request.getAccountId();

                try {
                    DatabaseConnector.setAccountActive(accountId, true);
                } catch (Exception e) {
                    System.err.println("Customer Support: Failed to activate account: " + e.getMessage());
                    return false;
                }
            }
        } else if (nextHandler != null) {
            return nextHandler.handleRequest(request);
        }
        return true;
    }
}
