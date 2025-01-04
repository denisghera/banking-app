package ro.uvt.dp.server;

import ro.uvt.dp.database.DatabaseConnector;
import ro.uvt.dp.entities.Bank;

import java.io.*;
import java.net.*;
import java.util.Map;

public class BankServer {
    public static String bankCode;
    private static ServerSocket serverSocket;
    private static Bank bank;
    private static int clientCounter = 0;

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Bank code argument is missing!");
                return;
            }

            bankCode = args[0];
            int PORT = BankPortMapper.getPortForBank(bankCode);

            if ("CS".equalsIgnoreCase(bankCode)) {
                System.out.println("Customer Support Server is starting...");
            } else {
                bank = Bank.getInstance(bankCode);
                System.out.println("Bank server for bank code " + bankCode + " is up and running...");
            }

            serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCounter++;
                System.out.println("New client #" + clientCounter + " connected!");

                if ("CS".equalsIgnoreCase(bankCode)) {
                    new SupportClientHandler(clientSocket, clientCounter).start();
                } else {
                    new ClientHandler(clientSocket, bank, clientCounter).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error initializing the bank: " + e.getMessage());
        }
    }

    static class SupportClientHandler extends Thread {
        private Socket clientSocket;
        private int clientNumber;

        public SupportClientHandler(Socket socket, int clientNumber) {
            this.clientSocket = socket;
            this.clientNumber = clientNumber;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Client #" + clientNumber + " request: " + request);

                    String[] parts = request.split("\\|");
                    String command = parts[0];

                    switch (command) {
                        case "LOGIN":
                            handleLogin(parts[1], parts[2], out);
                            break;
                        default:
                            out.println("ERROR: Unknown command");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleLogin(String username, String password, PrintWriter out) {
            try {
                if (!DatabaseConnector.validateLogin(username, password)) {
                    out.println("ERROR: Invalid credentials");
                    return;
                }

                Map<String, String> roleDetails = DatabaseConnector.getUserRole(username);
                if (roleDetails != null) {
                    String supportLevel = roleDetails.get("support_level");
                    if ("customer support".equalsIgnoreCase(supportLevel) || "admin".equalsIgnoreCase(supportLevel)) {
                        out.println("SUCCESS|" + supportLevel);
                        System.out.println("Support client #" + clientNumber + " (" + username + ") logged in.");
                        return;
                    }
                }

                out.println("ERROR: Unauthorized access for support");
            } catch (Exception e) {
                out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private Bank bank;
        private int clientNumber;

        public ClientHandler(Socket socket, Bank bank, int clientNumber) {
            this.clientSocket = socket;
            this.bank = bank;
            this.clientNumber = clientNumber;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Client #" + clientNumber + " request: " + request);

                    String[] parts = request.split("\\|");
                    String command = parts[0];

                    switch (command) {
                        case "LOGIN":
                            handleLogin(parts[1], parts[2]);
                            break;
                        case "SIGNUP":
                            handleSignup(parts);
                            break;
                        default:
                            out.println("ERROR: Unknown command");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null) clientSocket.close();
                    System.out.println("Client #" + clientNumber + " disconnected...");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleLogin(String username, String password) {
            try {
                if (!DatabaseConnector.validateLogin(username, password)) {
                    out.println("ERROR: Invalid credentials");
                    return;
                }

                out.println("SUCCESS: Login successful");
                System.out.println("Client #" + clientNumber + " (" + username + ") logged in.");
            } catch (Exception e) {
                out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void handleSignup(String[] parts) {
            try {
                if (parts.length != 6) {
                    out.println("ERROR: Invalid SIGNUP request format");
                    return;
                }

                String username = parts[1];
                String fullname = parts[2];
                String email = parts[3];
                String address = parts[4];
                String password = parts[5];

                if (DatabaseConnector.isUsernameTaken(username)) {
                    out.println("ERROR: Username is already taken");
                    return;
                }

                if (!DatabaseConnector.addUser(username, fullname, email, address, password, bank.getBankCode())) {
                    out.println("ERROR: Problem at registering. Please try again\nIf the problem persists, submit a ticket!");
                    return;
                }
                out.println("SUCCESS: User registered successfully");
                System.out.println("Client #" + clientNumber + " (" + username + ") registered.");
            } catch (Exception e) {
                out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}