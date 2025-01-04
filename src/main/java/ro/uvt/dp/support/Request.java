package ro.uvt.dp.support;

import ro.uvt.dp.entities.Account;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private final Priority priority;
    private final String accountId;
    private boolean resolved;
    private final LocalDateTime timestamp;
    private final String message;

    public enum Priority {
        NORMAL, CRITICAL
    }

    public Request(Priority priority, String accountId, String message) {
        this.priority = priority;
        this.accountId = accountId;
        this.resolved = false;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getMessage() {
        return this.message;
    }

    public Double getDepositSum() {
        if (message != null) {
            Pattern pattern = Pattern.compile("sum: (.+)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                try {
                    return Double.parseDouble(matcher.group(1));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid deposit sum format: " + e.getMessage());
                }
            }
        }
        return null;
    }
}
