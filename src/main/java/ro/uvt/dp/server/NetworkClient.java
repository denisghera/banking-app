package ro.uvt.dp.server;

import java.io.*;
import java.net.*;

public class NetworkClient implements AutoCloseable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public NetworkClient(String bankCode) throws IOException {
        Integer port = BankPortMapper.getPortForBank(bankCode);
        if (port == null) {
            throw new IOException("No server found for bank code: " + bankCode);
        }
        this.socket = new Socket("localhost", port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String login(String username, String password) throws IOException {
        String request = "LOGIN|" + username + "|" + password;
        out.println(request);
        return in.readLine();
    }

    public String signup(String username, String fullname, String email, String address, String password) throws IOException {
        String request = "SIGNUP|" + username + "|" + fullname + "|" + email + "|" + address + "|" + password;
        out.println(request);
        return in.readLine();
    }

    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
