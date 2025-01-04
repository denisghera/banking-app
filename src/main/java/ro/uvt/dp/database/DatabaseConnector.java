package ro.uvt.dp.database;

import ro.uvt.dp.accounts.AccountEUR;
import ro.uvt.dp.accounts.AccountEURFactory;
import ro.uvt.dp.accounts.AccountRON;
import ro.uvt.dp.accounts.AccountRONFactory;
import ro.uvt.dp.accounts.states.ActiveAccountState;
import ro.uvt.dp.accounts.states.ClosedAccountState;
import ro.uvt.dp.decorators.LifeInsuranceDecorator;
import ro.uvt.dp.decorators.RoundUpDecorator;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;
import ro.uvt.dp.services.AccountState;
import ro.uvt.dp.support.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=dp-banking;encrypt=false;trustServerCertificate=true";
    private static final String USER = "default_user";
    private static final String PASSWORD = "default_password";
    private static Connection connection = null;

    public static Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection to database established successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("JDBC Driver not found!");
                e.printStackTrace();
            }
        }
        return connection;
    }
    public static boolean addUser(String username, String fullname, String email, String address, String password, String bankID) {

        String credQuery = "INSERT INTO Credentials (username, password) VALUES (?, ?)";
        String clientQuery = "INSERT INTO Clients (username, full_name, email, address, no_of_accounts, bank_id) VALUES (?, ?, ?, ?, ?, ?)";
        String bankQuery = "UPDATE Banks SET no_of_clients = no_of_clients + 1 WHERE unique_code = ?";
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement credStmt = conn.prepareStatement(credQuery)) {
                credStmt.setString(1, username);
                credStmt.setString(2, password);
                credStmt.executeUpdate();
            }
            try (PreparedStatement clientStmt = conn.prepareStatement(clientQuery)) {
                clientStmt.setString(1, username);
                clientStmt.setString(2, fullname);
                clientStmt.setString(3, email);
                clientStmt.setString(4, address);
                clientStmt.setInt(5, 0);
                clientStmt.setString(6, bankID);
                clientStmt.executeUpdate();
            }
            try (PreparedStatement bankStmt = conn.prepareStatement(bankQuery)) {
                bankStmt.setString(1, bankID);
                bankStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }
    public static boolean validateLogin(String username, String password) {
        String query = "SELECT COUNT(*) FROM Credentials WHERE username = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM Credentials WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static List<String> getBankIDs() {
        List<String> bankIDs = new ArrayList<>();
        String query = "SELECT unique_code FROM Banks";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bankIDs.add(rs.getString("unique_code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankIDs;
    }
    public static List<Account> getClientAccounts(String username) {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT id, currency, balance, insurance_check, roundup_check, roundup_balance, active FROM Accounts WHERE client_username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String accountCode = rs.getString("id");
                    String currency = rs.getString("currency");
                    double amount = rs.getDouble("balance");
                    boolean insurance = rs.getBoolean("insurance_check");
                    boolean roundupCheck = rs.getBoolean("roundup_check");
                    double roundupBalance = rs.getDouble("roundup_balance");
                    boolean isActive = rs.getBoolean("active");
                    AccountState state;
                    if (isActive) {
                        state = new ActiveAccountState();
                    } else {
                        state = new ClosedAccountState();
                    }
                    Account account;
                    switch (currency) {
                        case "EUR":
                            account = new AccountEURFactory().create(accountCode, amount, state);
                            break;
                        case "RON":
                            account = new AccountRONFactory().create(accountCode, amount, state);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported currency: " + currency);
                    }

                    if (insurance) {
                        account = new LifeInsuranceDecorator(account);
                        ((LifeInsuranceDecorator) account).updateLifeInsurance(true, 10000);
                    }
                    if (roundupCheck) {
                        account = new RoundUpDecorator(account);
                        ((RoundUpDecorator) account).addRoundUpBalance(roundupBalance);
                    }

                    accounts.add(account);
                }
            }
        } catch (SQLException | InvalidAmountException e) {
            e.printStackTrace();
        }
        return accounts;
    }
    public static Map<String, String> getUserDetails(String username) {
        String query = "SELECT username, full_name, address, email, bank_id FROM Clients WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> userDetails = new HashMap<>();
                    userDetails.put("username", rs.getString("username"));
                    userDetails.put("name", rs.getString("full_name"));
                    userDetails.put("address", rs.getString("address"));
                    userDetails.put("email", rs.getString("email"));
                    userDetails.put("bankID", rs.getString("bank_id"));
                    return userDetails;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void addAccount(String clientUsername, Account account, String currency) {
        String accountQuery = "INSERT INTO Accounts (id, client_username, currency, balance, insurance_check, roundup_check, roundup_balance, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateClientQuery = "UPDATE Clients SET no_of_accounts = no_of_accounts + 1 WHERE username = ?";
        String updateBankQuery = "UPDATE Banks SET no_of_accounts = no_of_accounts + 1 WHERE unique_code = (SELECT bank_id FROM Clients WHERE username = ?)";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(accountQuery)) {
                stmt.setString(1, account.getAccountCode());
                stmt.setString(2, clientUsername);
                stmt.setString(3, currency);
                stmt.setDouble(4, account.getAmount());
                stmt.setBoolean(5, false);
                stmt.setBoolean(6, false);
                stmt.setDouble(7, 0);
                stmt.setBoolean(8, false);
                stmt.executeUpdate();
            }

            try (PreparedStatement updateClientStmt = conn.prepareStatement(updateClientQuery)) {
                updateClientStmt.setString(1, clientUsername);
                updateClientStmt.executeUpdate();
            }

            try (PreparedStatement updateBankStmt = conn.prepareStatement(updateBankQuery)) {
                updateBankStmt.setString(1, clientUsername);
                updateBankStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
    public static void updateDatabaseOnOperation(Account account) {
        String query = "UPDATE Accounts SET balance = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, account.getAmount());
            stmt.setString(2, account.getAccountCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update database: " + e.getMessage());
            throw new RuntimeException("Database update failed", e);
        }
    }
    public static void updateRoundupBalanceInDatabase(String accountCode, double roundUpBalance) {
        String query = "UPDATE Accounts SET roundup_balance = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, roundUpBalance);
            stmt.setString(2, accountCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update roundup_balance in database: " + e.getMessage());
            throw new RuntimeException("Database update failed", e);
        }
    }

    public static void setAccountActive(String accountId, boolean isActive) {
        String query = "UPDATE Accounts SET active = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isActive);
            stmt.setString(2, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }
    public static Account findAccountById(String accountId) {
        Account account = null;
        String query = "SELECT * FROM Accounts WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String accountCode = resultSet.getString("id");
                double balance = resultSet.getDouble("balance");
                String currency = resultSet.getString("currency");
                boolean stateBit = resultSet.getBoolean("active");
                AccountState state = stateBit ? new ActiveAccountState() : new ClosedAccountState();
                if (currency.equals("RON")) {
                    account = new AccountRON(accountCode, balance, state);
                } else if (currency.equals("EUR")) {
                    account = new AccountEUR(accountCode, balance, state);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidAmountException e) {
            throw new RuntimeException(e);
        }

        return account;
    }
    public static void saveTransaction(String operationType, String initiatorId, String recipientId, double amount, String message) {
        String transactionQuery = "INSERT INTO Transactions (type, initiator_id, recipient_id, amount, message) VALUES (?, ?, ?, ?, ?)";
        String clientQuery = "SELECT client_username FROM Accounts WHERE id = ?";
        String bankQuery = "UPDATE Banks SET no_of_transactions = no_of_transactions + 1 WHERE unique_code = (SELECT bank_id FROM Clients WHERE username = ?)";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                stmt.setString(1, operationType);
                stmt.setString(2, initiatorId);
                if (recipientId != null) {
                    stmt.setString(3, recipientId);
                } else {
                    stmt.setString(3, "");
                }
                stmt.setDouble(4, amount);
                stmt.setString(5, message);
                stmt.executeUpdate();
            }

            String clientUsername = null;
            try (PreparedStatement clientStmt = conn.prepareStatement(clientQuery)) {
                clientStmt.setString(1, initiatorId);
                try (ResultSet rs = clientStmt.executeQuery()) {
                    if (rs.next()) {
                        clientUsername = rs.getString("client_username");
                    }
                }
            }

            if (clientUsername != null) {
                try (PreparedStatement bankStmt = conn.prepareStatement(bankQuery)) {
                    bankStmt.setString(1, clientUsername);
                    bankStmt.executeUpdate();
                }
            } else {
                System.err.println("Client username not found for account ID: " + initiatorId);
            }

            conn.commit();
            System.out.println("Transaction saved and bank transaction count updated.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
    public static void setAccountInsurance(String accountId) {
        String query = "UPDATE Accounts SET insurance_check = 1 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }
    public static void setAccountRoundup(String accountId) {
        String query = "UPDATE Accounts SET roundup_check = 1 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }
    public static Map<String, String> getUserRole(String username) {
        String query = "SELECT support_level FROM Roles WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Map.of(
                            "support_level", rs.getString("support_level")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<Map<String, String>> getTicketsForSupportLevel(String supportLevel) {
        String query;
        if ("customer support".equalsIgnoreCase(supportLevel)) {
            query = "SELECT id, priority, message, timestamp, resolved, account_id FROM Tickets WHERE priority = 'NORMAL' AND resolved = 0";
        } else if ("admin".equalsIgnoreCase(supportLevel)) {
            query = "SELECT id, priority, message, timestamp, resolved, account_id FROM Tickets WHERE priority = 'CRITICAL' AND resolved = 0";
        } else {
            throw new IllegalArgumentException("Unsupported support level: " + supportLevel);
        }

        List<Map<String, String>> tickets = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> ticket = new HashMap<>();
                ticket.put("id", String.valueOf(rs.getInt("id")));
                ticket.put("priority", rs.getString("priority"));
                ticket.put("message", rs.getString("message"));
                ticket.put("timestamp", rs.getString("timestamp"));
                ticket.put("resolved", rs.getString("resolved"));
                ticket.put("account_id", rs.getString("account_id"));
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch tickets: " + e.getMessage());
        }
        return tickets;
    }
    public static boolean resolveTicket(String ticketId) {
        String query = "UPDATE Tickets SET resolved = 1 WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(ticketId));
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Failed to resolve ticket: " + e.getMessage());
            return false;
        }
    }
    public static void createTicket(Request request) {
        String priority = request.getPriority().toString();
        String message = request.getMessage();
        String accountId = request.getAccountId();

        String query = "INSERT INTO Tickets (priority, message, resolved, account_id) VALUES (?, ?, 0, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, priority);
            stmt.setString(2, message);
            stmt.setString(3, accountId);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Ticket created successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to create ticket: " + e.getMessage());
        }
    }
    public static boolean creditAccount(String accountId, double amount) {
        String query = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, amount);
            stmt.setString(2, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to credit account: " + e.getMessage());
            return false;
        }
        return true;
    }
}
