package ro.uvt.dp.database;

import ro.uvt.dp.accounts.AccountEURFactory;
import ro.uvt.dp.accounts.AccountRONFactory;
import ro.uvt.dp.decorators.LifeInsuranceDecorator;
import ro.uvt.dp.decorators.RoundUpDecorator;
import ro.uvt.dp.entities.Account;
import ro.uvt.dp.exceptions.InvalidAmountException;

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
                    return rs.getInt(1) > 0; // Username exists if count > 0
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

    public static String getUserBankID(String username) {
        String bankID = null;
        String query = "SELECT bank_id FROM Clients WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bankID = rs.getString("bank_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankID;
    }
    public static List<Account> getClientAccounts(String username) {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT id, currency, balance, insurance_check, roundup_check, roundup_balance FROM Accounts WHERE client_username = ?";

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

                    Account account;
                    switch (currency) {
                        case "EUR":
                            account = new AccountEURFactory().create(accountCode, amount);
                            break;
                        case "RON":
                            account = new AccountRONFactory().create(accountCode, amount);
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
        String query = "INSERT INTO Accounts (id, client_username, currency, balance, insurance_check, roundup_check, roundup_balance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, account.getAccountCode());
            stmt.setString(2, clientUsername);
            stmt.setString(3, currency);
            stmt.setDouble(4, account.getAmount());
            stmt.setBoolean(5, false);
            stmt.setBoolean(6, false);
            stmt.setDouble(7, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
