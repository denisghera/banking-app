package ro.uvt.dp.support;

import ro.uvt.dp.entities.Account;

public class Request {
    private final Priority priority;
    private final Account account;

    public enum Priority {
        BASIC, CRITICAL
    }
    public Request(Priority priority, Account account) {
        this.priority = priority;
        this.account = account;
    }
    public Priority getPriority() {
        return this.priority;
    }
    public Account getAccount() {
        return this.account;
    }
}
