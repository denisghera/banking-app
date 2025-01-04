package ro.uvt.dp.support;

import ro.uvt.dp.services.SupportHandler;
import ro.uvt.dp.database.DatabaseConnector;

public class AdminSupport implements SupportHandler {
    private SupportHandler nextHandler;

    public void setNextHandler(SupportHandler nextHandler) {
        // No next handler -> leave empty for now
    }

    public boolean handleRequest(Request request) {
        if (request.getPriority() == Request.Priority.CRITICAL) {
            String message = request.getMessage();

            if (message != null && message.toLowerCase().contains("large deposit")) {
                String accountId = request.getAccountId();

                try {
                    double depositAmount = request.getDepositSum();
                    return DatabaseConnector.creditAccount(accountId, depositAmount);
                } catch (Exception e) {
                    System.err.println("Admin Support: Failed to credit account: " + e.getMessage());
                    return false;
                }
            }
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
        return true;
    }
}
