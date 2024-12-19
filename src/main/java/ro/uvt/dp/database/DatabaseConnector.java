package ro.uvt.dp.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}
